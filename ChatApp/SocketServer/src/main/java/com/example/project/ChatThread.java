package com.example.project;

import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.Message;
import com.example.project.Serializable.UserCredentials;
import com.example.project.SessionManager.SessionManager;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
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

        UserCredentials userCredentials = null;
        SessionManager sessionManager = SessionManager.getInstance();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            userCredentials = (UserCredentials) ois.readObject();
            String username = userCredentials.getUsername();
            sessionManager.addMember(username, clientSocket);
            sessionManager.printClientTracker();
            System.out.println(userCredentials.getUsername() + " connected.");

            oos.writeObject(buildBuddyList(username));

            ObjectOutputStream toClient = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream fromClient = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                Message clientInbound = (Message) fromClient.readObject();
                if (!clientInbound.isNullMessage()) {
                    String recipient = clientInbound.getRecipient();
                    System.out.println("Inbound: " + clientInbound.getMessage());
                    if (sessionManager.isOnline(recipient)) {
                        sessionManager.addOutgoingMessage(recipient, clientInbound);
                        System.out.println(recipient);
                    }
                }
                Message clientOutbound = sessionManager.getNextOutgoing(username);
                if (!clientOutbound.isNullMessage()) System.out.println("Outbound: " + clientOutbound.getMessage());
                if (clientInbound.isBuddyListUpdate()) {
                    clientOutbound.setBuddyList(buildBuddyList(username));
                    clientOutbound.setBuddyListUpdate(true);
                }
                toClient.writeObject(clientOutbound);
                toClient.flush();
            }

        } catch (SocketException se) {

        } catch (IOException ioe) {

        } catch (ClassNotFoundException cnfe) {

        } finally {
            try {
                System.out.println(userCredentials.getUsername() + " disconnected.");
                clientSocket.close();
                sessionManager.removeMember(userCredentials.getUsername());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private BuddyList buildBuddyList(String username) {
        ArrayList<String> buddies = new ArrayList<>();
        buddies.add("Carl");
        buddies.add("Joan");
        buddies.add("Bob");
        BuddyList buddyList = new BuddyList(buddies);

        ArrayList<String> currentlyOffline = new ArrayList<>();
        ArrayList<String> currentlyOnline = new ArrayList<>();

        SessionManager sessionManager = SessionManager.getInstance();
        for (String buddy : buddyList.getBuddies()) {
            if (sessionManager.isOnline(buddy)) currentlyOnline.add(buddy);
            else currentlyOffline.add(buddy);
        }
        buddyList.setCurrentlyOffline(currentlyOffline);
        buddyList.setCurrentlyOnline(currentlyOnline);
        return buddyList;
    }
}
