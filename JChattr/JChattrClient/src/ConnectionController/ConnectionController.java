package ConnectionController;

import com.example.project.ChatWindow.ChatWindow;
import com.example.project.ChatWindow.ChatWindowController;
import com.example.project.ChatroomScreen.ChatroomScreen;
import com.example.project.PasswordSalter.PasswordSalter;
import com.example.project.Serializable.*;
import com.example.project.SessionManager.SessionManager;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionController {
    private String hostName;
    private int portNumber;

    public ConnectionController(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void pingServer(int operation, String username, String password) {
        try {
            Socket clientSocket = new Socket(hostName, portNumber);
            SessionManager.getInstance().setClientSocket(clientSocket);

            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            ServerHello serverHello = new ServerHello();

            if (operation == 0) serverHello.setRequestUserCreation(true);
            else if (operation == 1) serverHello.setRequestLogin(true);

            oos.writeObject(serverHello);
            oos.flush();

            serverHello = (ServerHello) ois.readObject();

            if (operation == 0) createAccount(clientSocket, username, password);
            else if (operation == 1) verifyCredentials(clientSocket, username, password);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public void createAccount(Socket clientSocket, String username, String password) {
        try {
            PasswordSalter passwordSalter = new PasswordSalter();
            String passwordSalt = passwordSalter.getRandomSalt();
            String passwordSaltedHash = passwordSalter.getHash(password, passwordSalt);

            UserCredentials userCredentials = new UserCredentials(username, passwordSaltedHash, passwordSalt);

            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            oos.writeObject(userCredentials);
            oos.flush();

            userCredentials = (UserCredentials) ois.readObject();

            if (userCredentials.isRequestAccepted()) System.out.println("Account created.");
            else System.out.println("Account creation failed.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public void verifyCredentials(Socket clientSocket, String username, String password) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            UserCredentials userCredentials = new UserCredentials(username);
            oos.writeObject(userCredentials);
            oos.flush();

            userCredentials = (UserCredentials) ois.readObject();
            String passwordSalt = userCredentials.getPasswordSalt();

            PasswordSalter passwordSalter = new PasswordSalter();
            String passwordSaltedHash = passwordSalter.getHash(password, passwordSalt);

            userCredentials.setPasswordSaltedHash(passwordSaltedHash);

            oos.writeObject(userCredentials);
            oos.flush();

            userCredentials = (UserCredentials) ois.readObject();

            if (userCredentials.isRequestAccepted()) {
                String displayName = userCredentials.getDisplayName();

                SessionManager.getInstance().setUsername(username);
                SessionManager.getInstance().setDisplayName(displayName);

                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(clientSocket.getInputStream());

                BuddyList buddyList = new BuddyList();
                oos.writeObject(buddyList);
                oos.flush();

                buddyList = (BuddyList) ois.readObject();

                SessionManager.getInstance().setBuddyList(buddyList);
                Platform.runLater(() -> SessionManager.getInstance().getWelcomeScreenController().showBuddyList());
                establishedConnection(clientSocket, username, displayName);

            } else System.out.println("Authentication Unsuccessful.");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public void establishedConnection(Socket clientSocket, String username, String displayName) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            int sleepInterval = 50; // milliseconds
            while (true) {
                Message serverOutbound = (SessionManager.getInstance().getOutgoingQueue().size() == 0) ?
                        new Message(true) : SessionManager.getInstance().getOutgoingQueue().remove();

                oos.writeObject(serverOutbound);
                oos.flush();

                Message serverInbound = (Message) ois.readObject();

                if (serverInbound.isBuddyListUpdate()) {
                    String buddyUsername = serverInbound.getBuddyList().getBuddies().get(0).getUsername();
                    String buddyDisplayName = serverInbound.getBuddyList().getBuddies().get(0).getDisplayName();
                    String buddyGroupName = serverInbound.getBuddyList().getBuddies().get(0).getGroupName();
                    boolean buddyOnline = (serverInbound.getBuddyList().getCurrentlyOnline().size() == 0);

                    if (serverInbound.getBuddyList().isAddUser()) {
                        Platform.runLater(() -> SessionManager.getInstance().getBuddyListScreenController().addUserToBuddyList(buddyUsername, buddyDisplayName, buddyGroupName, buddyOnline));
                    } else if (serverInbound.getBuddyList().isDeleteUser()) {
                        Platform.runLater(() -> SessionManager.getInstance().getBuddyListScreenController().deleteUserFromBuddyList(buddyUsername, buddyGroupName));
                    }
                }
                if (serverInbound.isChatroomListingsRequest()) {
                    if (SessionManager.getInstance().getChatroomWelcomeScreenController() != null) {
                        Platform.runLater(() -> SessionManager.getInstance().getChatroomWelcomeScreenController().fillCategories(
                                serverInbound.getCategories(),
                                serverInbound.getUsers()
                        ));
                    }
                }
                if (serverInbound.isEnteredChatroom()) {
                    String senderDisplayName = serverInbound.getSenderDisplayName();
                    if (username.equalsIgnoreCase(senderDisplayName)) {
                        if (SessionManager.getInstance().getChatroomController() != null) {
                            Platform.runLater(() -> SessionManager.getInstance().getChatroomController().shutdown());
                        }
                        ChatroomScreen chatroomScreen = new ChatroomScreen();
                        chatroomScreen.initData(
                                serverInbound.getChatroomCategoryAndName().getKey(),
                                serverInbound.getChatroomCategoryAndName().getValue(),
                                serverInbound.getChatroomUsers(),
                                "TestAdmin"
                        );
                        Platform.runLater(() -> chatroomScreen.start(new Stage()));
                    } else {
                        Platform.runLater(() -> SessionManager.getInstance().getChatroomController().addUser(senderDisplayName));
                        Platform.runLater(() -> SessionManager.getInstance().getChatroomController().appendUpdateMessage(senderDisplayName, "entered the chatroom."));
                    }
                }
                if (serverInbound.isLeftChatroom()) {
                    if (SessionManager.getInstance().getChatroomController() != null) {
                        Platform.runLater(() -> SessionManager.getInstance().getChatroomController().removeUser(serverInbound.getSenderDisplayName()));
                        Platform.runLater(() -> SessionManager.getInstance().getChatroomController().appendUpdateMessage(serverInbound.getSenderDisplayName(), "left the chatroom."));
                    }
                }
                if (serverInbound.isCarryingChatroomMessage()) {
                    if (SessionManager.getInstance().getChatroomController() != null
                            && SessionManager.getInstance().getChatroomController().getChatroomName()
                            .equals(serverInbound.getChatroomCategoryAndName().getValue())) {
                        Platform.runLater(() -> SessionManager.getInstance().getChatroomController().appendMessage(serverInbound.getSenderDisplayName(), serverInbound.getChatroomMessage()));
                    }
                }
                if (serverInbound.isLogOnEvent()) {
                    String userLoggedIn = serverInbound.getLogOn();
                    Platform.runLater(() -> SessionManager.getInstance().getBuddyListScreenController().updateBuddyList(userLoggedIn, 0));
                }
                if (serverInbound.isLogOutEvent()) {
                    String userLoggedOut = serverInbound.getLogOut();
                    Platform.runLater(() -> SessionManager.getInstance().getBuddyListScreenController().updateBuddyList(userLoggedOut, 1));
                }
                if (!serverInbound.isNullMessage()) {
                    ChatWindowController controller =
                            SessionManager.getInstance().getChatWindowController(username, serverInbound.getSender());
                    if (controller != null) {
                        Platform.runLater(() -> controller.appendText(serverInbound.getSender(), serverInbound.getMessage()));
                    } else {
                        ChatWindow chatWindow = new ChatWindow();
                        chatWindow.initData(username, serverInbound.getSenderDisplayName());
                        try {
                            Platform.runLater(() -> chatWindow.start());
                            Platform.runLater(() -> SessionManager.getInstance().getChatWindowController(username, serverInbound.getSenderDisplayName()).appendText(serverInbound.getSender(), serverInbound.getMessage()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                Thread.sleep(sleepInterval);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

}
