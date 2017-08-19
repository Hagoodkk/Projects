package com.example.project;

import com.example.project.DatabaseManager.DatabaseManager;
import com.example.project.Serializable.*;
import com.example.project.SessionManager.SessionManager;
import javafx.util.Pair;
import sun.net.ConnectionResetException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Kyle on 7/4/2017.
 */

public class ChatThread implements Runnable {

    private Socket clientSocket;

    public ChatThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        int requestType = pingClient();
        if (requestType == 0) {
            accountCreation();
        } else if (requestType == 1){
            String verifiedUsername = verifyCredentials();
            if (verifiedUsername != null) establishedConnection(verifiedUsername);
            else System.out.println("Connection failed.");
        } else if (requestType == -1) {
            System.out.println("Connection failed.");
        }
    }

    private boolean accountCreation() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            UserCredentials userCredentials = (UserCredentials) ois.readObject();
            String username = userCredentials.getUsername();
            String passwordSaltedHash = userCredentials.getPasswordSaltedHash();
            String passwordSalt = userCredentials.getPasswordSalt();

            System.out.println(passwordSalt);

            boolean accountCreated = false;

            if (isValid(username) && isValid(passwordSaltedHash) && isValid(passwordSalt)) {
                DatabaseManager databaseManager = DatabaseManager.getInstance();
                accountCreated = (databaseManager.addUser(username, passwordSaltedHash, passwordSalt));
            }

            if (accountCreated) {
                userCredentials.setRequestAccepted(true);
                oos.writeObject(userCredentials);
                oos.flush();
                return true;
            } else {
                userCredentials.setRequestAccepted(false);
                oos.writeObject(userCredentials);
                oos.flush();
                return false;
            }

        } catch (IOException ioe) {
            return false;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return false;
        }
    }

    private boolean isValid(String entry) {
        if (entry.contains(" ")) return false;
        if (entry.length() == 0) return false;
        return true;
    }

    private int pingClient() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            ServerHello serverHello = (ServerHello) ois.readObject();
            oos.writeObject(new ServerHello());
            oos.flush();
            if (serverHello.isRequestUserCreation()) return 0;
            if (serverHello.isRequestLogin()) return 1;
            else return -1;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return -1;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return -1;
        }
    }

    private String verifyCredentials() {
        UserCredentials userCredentials =  null;
        SessionManager sessionManager = SessionManager.getInstance();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            userCredentials = (UserCredentials) ois.readObject();
            String username = userCredentials.getUsername();

            DatabaseManager databaseManager = DatabaseManager.getInstance();
            String salt = databaseManager.getUserSalt(username);
            userCredentials.setPasswordSalt(salt);

            oos.writeObject(userCredentials);
            oos.flush();

            userCredentials = (UserCredentials) ois.readObject();
            if (databaseManager.comparePasswordSaltedHash(userCredentials.getUsername(), userCredentials.getPasswordSaltedHash())
                    && !sessionManager.isOnline(username)) {
                userCredentials.setDisplayName(databaseManager.getUserDisplayName(username));
                userCredentials.setRequestAccepted(true);
                oos.writeObject(userCredentials);
                oos.flush();

                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());

                BuddyList buddyList = (BuddyList) ois.readObject();

                sessionManager.removeStateUpdatesFromUser(username);

                oos.writeObject(buildBuddyList(username));

                sessionManager.addMember(username, clientSocket);
                sessionManager.printClientTracker();
                System.out.println(userCredentials.getUsername() + " connected.");
                return username;
            } else {
                userCredentials.setRequestAccepted(false);
                oos.writeObject(userCredentials);
                oos.flush();
                System.out.println("Connection refused.");
                return null;
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return null;
        }
    }

    private void establishedConnection(String username) {
        SessionManager sessionManager = SessionManager.getInstance();

        sessionManager.broadcastStateUpdate(username, 0);

        try {
            ObjectOutputStream toClient = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream fromClient = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                Message clientInbound = (Message) fromClient.readObject();
                if (!clientInbound.isNullMessage()) {
                    String recipient = clientInbound.getRecipient();
                    System.out.println("Inbound: " + clientInbound.getMessage());
                    if (sessionManager.isOnline(recipient)) {
                        sessionManager.addOutgoingMessage(recipient, clientInbound);
                    }
                    else {
                        sessionManager.addOutgoingMessage(recipient, clientInbound);
                    }
                }

                Message clientOutbound = sessionManager.getNextOutgoing(username);
                if (!clientOutbound.isNullMessage()) System.out.println("Outbound: " + clientOutbound.getMessage());
                if (clientInbound.isBuddyListUpdate()) {
                    String user = clientInbound.getSender();
                    String buddyUsername = clientInbound.getBuddyList().getBuddies().get(0).getUsername().toLowerCase();
                    String groupName = clientInbound.getBuddyList().getBuddies().get(0).getGroupName();
                    DatabaseManager databaseManager = DatabaseManager.getInstance();
                    if (clientInbound.getBuddyList().isAddUser()) {
                        boolean buddyAdded = databaseManager.addBuddyToUser(user, buddyUsername, groupName);
                        if (buddyAdded) {
                            String buddyDisplayName = databaseManager.getUserDisplayName(buddyUsername);

                            BuddyList buddyList = new BuddyList();
                            buddyList.setAddUser(true);
                            Buddy buddy = new Buddy(buddyUsername, buddyDisplayName, groupName);
                            buddyList.addBuddy(buddy);
                            if (sessionManager.isOnline(buddyUsername)) {
                                buddyList.getCurrentlyOnline().add(buddy);
                            } else {
                                buddyList.getCurrentlyOffline().add(buddy);
                            }
                            clientOutbound.setBuddyList(buddyList);
                            clientOutbound.setBuddyListUpdate(true);
                        } else System.out.println("Buddy addition failed.");

                    } else if (clientInbound.getBuddyList().isDeleteUser()) {
                        boolean buddyDeleted = databaseManager.removeBuddyFromUser(user, buddyUsername);
                        if (buddyDeleted) {
                            String buddyDisplayName = databaseManager.getUserDisplayName(buddyUsername);
                            BuddyList buddyList = new BuddyList();
                            buddyList.setDeleteUser(true);
                            Buddy buddy = new Buddy(buddyUsername, buddyDisplayName, groupName);
                            buddyList.addBuddy(buddy);
                            clientOutbound.setBuddyList(buddyList);
                            clientOutbound.setBuddyListUpdate(true);
                        } else System.out.println("Buddy deletion failed.");
                    }
                }

                if (clientInbound.isChatroomListingsRequest()) {
                    clientOutbound.setChatroomListingsRequest(true);
                    clientOutbound.setCategories(SessionManager.getInstance().getChatroomCategories());
                    clientOutbound.setUsers(SessionManager.getInstance().getChatroomUsers());
                    clientOutbound.setNullMessage(true);
                }
                if (clientInbound.isChatroomCreateRequest()) {
                    createChatroom(clientInbound.getSenderDisplayName(), clientInbound.getChatroomCategoryAndName());
                }
                if (clientInbound.isLeftChatroom()) {
                    String chatroomName = clientInbound.getChatroomCategoryAndName().getValue();

                    SessionManager.getInstance().removeChatroomUser(clientInbound.getSenderDisplayName(), clientInbound.getChatroomCategoryAndName());
                    ArrayList<String> usersInRoom = SessionManager.getInstance().getChatroomUsers().get(chatroomName);

                    Message message = new Message(true);
                    message.setLeftChatroom(true);
                    message.setChatroomCategoryAndName(clientInbound.getChatroomCategoryAndName());
                    message.setSenderDisplayName(clientInbound.getSenderDisplayName());
                    if (usersInRoom != null) {
                        for (String user : usersInRoom) {
                            SessionManager.getInstance().addOutgoingMessage(user.toLowerCase(), message);
                        }
                    }
                }

                if (clientInbound.isEnteredChatroom()) {
                    String chatroomCategory = clientInbound.getChatroomCategoryAndName().getKey();
                    String chatroomName = clientInbound.getChatroomCategoryAndName().getValue();
                    String displayName = clientInbound.getSenderDisplayName();

                    SessionManager.getInstance().addChatroomUser(displayName, chatroomCategory, chatroomName);

                    Message message = new Message(true);
                    message.setEnteredChatroom(true);
                    message.setSenderDisplayName(clientInbound.getSenderDisplayName());
                    message.setChatroomCategoryAndName(clientInbound.getChatroomCategoryAndName());
                    message.setChatroomUsers(SessionManager.getInstance().getChatroomUsers().get(chatroomName));

                    if (SessionManager.getInstance().getChatroomUsers().get(chatroomName) == null) {
                        createChatroom(clientInbound.getSenderDisplayName(), clientInbound.getChatroomCategoryAndName());
                        message.setChatroomUsers(SessionManager.getInstance().getChatroomUsers().get(chatroomName));
                    }
                    for (String user : SessionManager.getInstance().getChatroomUsers().get(chatroomName)) {
                        SessionManager.getInstance().addOutgoingMessage(user.toLowerCase(), message);
                    }
                }
                if (clientInbound.isCarryingChatroomMessage()) {
                    Message message = clientInbound;
                    for (String user : SessionManager.getInstance().getChatroomUsers().get(clientInbound.getChatroomCategoryAndName().getValue())) {
                        SessionManager.getInstance().addOutgoingMessage(user, message);
                    }
                }
                toClient.writeObject(clientOutbound);
                toClient.flush();
                toClient.reset();
            }
        }
        catch (ConnectionResetException cre) {}
        catch (EOFException eofe) {}
        catch (IOException ioe) {
            ioe.printStackTrace();
        }  catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } finally {
            try {
                SessionManager.getInstance().removeUserFromAllChatrooms(username);
                sessionManager.broadcastStateUpdate(username, 1);
                System.out.println(username + " disconnected.");
                clientSocket.close();
                sessionManager.removeMember(username);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void createChatroom(String displayName, Pair<String, String> chatroomInfo) {
        SessionManager.getInstance().addChatroom(displayName, chatroomInfo);
    }

    private BuddyList buildBuddyList(String username) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        BuddyList buddyList = databaseManager.getBuddyList(username);

        ArrayList<Buddy> currentlyOffline = new ArrayList<>();
        ArrayList<Buddy> currentlyOnline = new ArrayList<>();

        SessionManager sessionManager = SessionManager.getInstance();
        for (Buddy buddy : buddyList.getBuddies()) {
            if (sessionManager.isOnline(buddy.getUsername())) {
                currentlyOnline.add(buddy);
            } else currentlyOffline.add(buddy);
        }
        buddyList.setCurrentlyOffline(currentlyOffline);
        buddyList.setCurrentlyOnline(currentlyOnline);
        return buddyList;
    }
}
