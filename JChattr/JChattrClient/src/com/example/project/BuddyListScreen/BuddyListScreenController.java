package com.example.project.BuddyListScreen;

import com.example.project.ChatWindow.ChatWindow;
import com.example.project.ChatWindow.ChatWindowController;
import com.example.project.ChatroomScreen.ChatroomScreen;
import com.example.project.ChatroomWelcomeScreen.ChatroomWelcomeScreen;
import com.example.project.Serializable.Buddy;
import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import com.example.project.WelcomeScreen.WelcomeScreenController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.Timer;

public class BuddyListScreenController {
    @FXML
    Parent root;
    @FXML
    TreeView buddy_list_tree;
    @FXML
    ImageView buddylist_icon;
    @FXML
    HBox buddylist_icon_hbox;

    private Timer timer;

    private SessionManager sessionManager = SessionManager.getInstance();
    private Socket clientSocket = sessionManager.getClientSocket();
    private String username = sessionManager.getUsername();

    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private HashMap<String, CustomTreeItem> groups;
    private HashMap<String, CustomTreeItem> onlineUsers = new HashMap<>();
    private HashMap<String, CustomTreeItem> offlineUsers = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("This is " + username + "'s debug screen.");

        buildBuddyList(sessionManager.getBuddyList());

        Media media = new Media(getClass().getClassLoader().getResource("sounds/395798__lipsumdolor__computer-startup.wav").toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();

        try {
            toServer = new ObjectOutputStream(clientSocket.getOutputStream());
            fromServer = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

            int timerInterval = 50;
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                    Message serverOutbound;
                    if (sessionManager.getOutgoingQueue().isEmpty()) {
                        serverOutbound = new Message(true);
                    } else {
                        serverOutbound = sessionManager.getOutgoingQueue().remove();
                    }

                    try {
                        toServer.writeObject(serverOutbound);
                        toServer.flush();
                        Message serverInbound = (Message) fromServer.readObject();

                        if (serverInbound.isBuddyListUpdate()) {
                            System.out.println("Buddy List update time");
                            String buddyUsername = serverInbound.getBuddyList().getBuddies().get(0).getUsername();
                            System.out.println(buddyUsername);
                            String buddyDisplayName = serverInbound.getBuddyList().getBuddies().get(0).getDisplayName();
                            String buddyGroupName = serverInbound.getBuddyList().getBuddies().get(0).getGroupName();

                            if (serverInbound.getBuddyList().isAddUser()) {
                                if (groups.get(buddyGroupName) == null) {
                                    CustomTreeItem groupItem = new CustomTreeItem(new HBox(), buddyGroupName, true, false);
                                    groups.get("root").getChildren().add(0, groupItem);
                                    groups.put(buddyGroupName, groupItem);
                                }

                                CustomTreeItem treeItem = new CustomTreeItem(new HBox(), buddyDisplayName, null);

                                if (serverInbound.getBuddyList().getCurrentlyOnline().size() == 0) {
                                    offlineUsers.put(buddyUsername, treeItem);
                                    groups.get("offline").getChildren().add(treeItem);
                                } else {
                                    onlineUsers.put(buddyUsername, treeItem);
                                    groups.get(buddyGroupName).getChildren().add(treeItem);
                                }
                                sessionManager.getBuddyList().addBuddy(new Buddy(buddyUsername, buddyDisplayName, buddyGroupName));
                            } else if (serverInbound.getBuddyList().isDeleteUser()) {
                                CustomTreeItem treeItem;
                                if ((treeItem = offlineUsers.get(buddyUsername)) != null) {
                                    offlineUsers.remove(buddyUsername);
                                    groups.get("offline").getChildren().remove(treeItem);
                                } else if ((treeItem = onlineUsers.get(buddyUsername)) != null) {
                                    onlineUsers.remove(treeItem);
                                    groups.get(buddyGroupName).getChildren().remove(treeItem);
                                }
                            }
                        }
                        if (serverInbound.isChatroomListingsRequest()) {
                            if (SessionManager.getInstance().getChatroomWelcomeScreenController() != null) {
                                SessionManager.getInstance().getChatroomWelcomeScreenController().fillCategories(
                                        serverInbound.getCategories(),
                                        serverInbound.getUsers()
                                );
                            }
                        }
                        if (serverInbound.isEnteredChatroom()) {
                            String displayName = serverInbound.getSenderDisplayName();
                            if (username.equalsIgnoreCase(displayName)) {
                                if (SessionManager.getInstance().getChatroomController() != null) SessionManager.getInstance().getChatroomController().shutdown();
                                    ChatroomScreen chatroomScreen = new ChatroomScreen();
                                    chatroomScreen.initData(
                                            serverInbound.getChatroomCategoryAndName().getKey(),
                                            serverInbound.getChatroomCategoryAndName().getValue(),
                                            serverInbound.getChatroomUsers(),
                                            "TestAdmin"
                                    );
                                    chatroomScreen.start(new Stage());
                            } else {
                                SessionManager.getInstance().getChatroomController().addUser(displayName);
                                SessionManager.getInstance().getChatroomController().appendUpdateMessage(displayName, "entered the chatroom.");
                            }
                        }
                        if (serverInbound.isLeftChatroom()) {
                            if (SessionManager.getInstance().getChatroomController() != null) {
                                SessionManager.getInstance().getChatroomController().removeUser(serverInbound.getSenderDisplayName());
                                SessionManager.getInstance().getChatroomController().appendUpdateMessage(serverInbound.getSenderDisplayName(), "left the chatroom.");
                            }
                        }
                        if (serverInbound.isCarryingChatroomMessage()) {
                            if (SessionManager.getInstance().getChatroomController() != null
                                    && SessionManager.getInstance().getChatroomController().getChatroomName()
                                    .equals(serverInbound.getChatroomCategoryAndName().getValue())) {
                                SessionManager.getInstance().getChatroomController().appendMessage(serverInbound.getSenderDisplayName(), serverInbound.getChatroomMessage());
                            }
                        }
                        if (serverInbound.isLogOnEvent()) {
                            updateBuddyList(serverInbound.getLogOn(), 0);
                        }
                        if (serverInbound.isLogOutEvent()) {
                            updateBuddyList(serverInbound.getLogOut(), 1);
                        }
                        if (!serverInbound.isNullMessage()) {
                            System.out.println(serverInbound.getMessage());
                            ChatWindowController controller =
                                    sessionManager.getChatWindowController(username, serverInbound.getSender());
                            if (controller != null) {
                                controller.appendText(serverInbound.getSender(), serverInbound.getMessage());
                            } else {
                                ChatWindow chatWindow = new ChatWindow();
                                chatWindow.initData(username, serverInbound.getSenderDisplayName());
                                try {
                                    chatWindow.start();
                                    ChatWindowController controller2 =
                                            sessionManager.getChatWindowController(username, serverInbound.getSender());
                                    controller2.appendText(serverInbound.getSender(), serverInbound.getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    catch (SocketException se) {
                        se.printStackTrace();
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (ClassNotFoundException cnfe) {
                        cnfe.printStackTrace();
                    }
                });
                }
            }, 0, timerInterval);

    }

    private void buildBuddyList(BuddyList buddyList) {

        CustomTreeItem rootItem = new CustomTreeItem(new HBox(), "Contacts", false, true);
        CustomTreeItem offline = new CustomTreeItem(new HBox(), "Offline", true, false);
        buddy_list_tree.setRoot(rootItem);

        groups = new HashMap<>();
        groups.put("root", rootItem);
        groups.put("offline", offline);
        for (Buddy buddy : buddyList.getBuddies()) {
            if (groups.get(buddy.getGroupName()) == null) {
                CustomTreeItem groupItem =  new CustomTreeItem(new HBox(), buddy.getGroupName(), true, false);
                groups.put(buddy.getGroupName(), groupItem);
                rootItem.getChildren().add(groupItem);
            }
        }

        for (Buddy buddy : buddyList.getCurrentlyOnline()) {
            System.out.println(buddy.getDisplayName());
            CustomTreeItem relevantGroup = groups.get(buddy.getGroupName());
            CustomTreeItem treeItem = new CustomTreeItem(new HBox(), buddy.getDisplayName(), null);
            onlineUsers.put(buddy.getUsername(), treeItem);
            relevantGroup.getChildren().add(treeItem);
        }

        rootItem.getChildren().add(offline);
        for (Buddy buddy : buddyList.getCurrentlyOffline()) {
            CustomTreeItem treeItem = new CustomTreeItem(new HBox(), buddy.getDisplayName(), null);
            offlineUsers.put(buddy.getUsername(), treeItem);
            offline.getChildren().add(treeItem);
        }

        buddylist_icon.setImage(new Image("images/penguin1.png"));
        buddylist_icon_hbox.setAlignment(Pos.CENTER);
        buddylist_icon_hbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 2, 2, 2))));
    }

    private void updateBuddyList(String sender, int state) {
        if (!sessionManager.getBuddyList().hasBuddy(sender)) return;
        String groupName = sessionManager.getBuddyList().getGroupName(sender);
        CustomTreeItem treeItem;

        if (state == 0) {
            System.out.println(sender + " connected.");
            if ((treeItem = offlineUsers.get(sender)) != null) {
                groups.get("offline").getChildren().remove(treeItem);
                groups.get(groupName).getChildren().add(treeItem);
                offlineUsers.remove(sender);
                onlineUsers.put(sender, treeItem);
                treeItem.setRecentlyLoggedOn();

            } else if ((treeItem = onlineUsers.get(sender)) != null) {
                treeItem.setRecentlyLoggedOn();
            }

        } else if (state == 1) {
            System.out.println(sender);
                System.out.println(sender + " disconnected.");
                if ((treeItem = onlineUsers.get(sender)) != null) {
                    treeItem.setRecentlyLoggedOff();
                    groups.get(groupName).getChildren().remove(treeItem);
                    groups.get("offline").getChildren().add(treeItem);
                    onlineUsers.remove(sender);
                    offlineUsers.put(sender, treeItem);
                }
        }
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        CustomTreeItem treeItem = (CustomTreeItem) buddy_list_tree.getSelectionModel().getSelectedItem();
        if (treeItem == null || !treeItem.isUser()) return;

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                String recipient = treeItem.getBuddyDisplayName();
                ChatWindowController chatWindowController = sessionManager.getChatWindowController(username, recipient);
                if (chatWindowController != null) {
                    chatWindowController.requestFocus();
                } else {
                    ChatWindow chatWindow = new ChatWindow();
                    chatWindow.initData(username, recipient);
                    try {
                        chatWindow.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void shutdown() {
        timer.cancel();
        timer.purge();
        /*
            This block of for loops MUST be kept in the program.
            The CustomTreeItem class uses Timers. Whenever timers
            are instantiated, it creates a thread whether or not
            any action has ever been scheduled on them. So if they
            are not nullified, the threads will never be cancelled
            which will create major memory leaks and will not allow
            the halting of the program.
         */
        for (CustomTreeItem treeItem : groups.values()) {
            treeItem.nullifyTimer();
        }
        for (CustomTreeItem treeItem : offlineUsers.values()) {
            treeItem.nullifyTimer();
        }
        for (CustomTreeItem treeItem : onlineUsers.values()) {
            treeItem.nullifyTimer();
        }
        SessionManager.getInstance().closeAllChatWindows();
        SessionManager.getInstance().getWelcomeScreenController().showStage();
        sessionManager.nullify();
    }

    public void handleAddBuddyButtonAction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Buddy");
        dialog.setHeaderText("Enter the buddy name.");
        Optional<String> result = dialog.showAndWait();
        String buddyNameInput;
        if (result.isPresent()) buddyNameInput = result.get();
        else return;

        Message message = new Message();
        message.setSender(username);
        message.setBuddyListUpdate(true);
        BuddyList buddyList = new BuddyList();
        buddyList.setAddUser(true);
        Buddy buddy = new Buddy(buddyNameInput.toLowerCase(), buddyNameInput, "Friends");
        buddyList.addBuddy(buddy);
        message.setBuddyList(buddyList);
        message.setNullMessage(true);
        sessionManager.addOutgoingMessage(message);
    }

    public void handleDeleteBuddyButtonAction() {
        CustomTreeItem treeItem = (CustomTreeItem) buddy_list_tree.getSelectionModel().getSelectedItem();
        if (treeItem == null || !treeItem.isUser()) return;
        String buddyName = treeItem.getBuddyDisplayName();
        Message message = new Message();
        message.setSender(username);
        message.setBuddyListUpdate(true);
        BuddyList buddyList = new BuddyList();
        buddyList.setDeleteUser(true);
        Buddy buddy = new Buddy(buddyName.toLowerCase(), buddyName, "Friends");
        buddyList.addBuddy(buddy);
        message.setBuddyList(buddyList);
        message.setNullMessage(true);
        sessionManager.addOutgoingMessage(message);
    }

    public void handleChatRoomTestButtonAction(MouseEvent mouseEvent) {
        ChatroomScreen chatroomScreen = new ChatroomScreen();
        chatroomScreen.initData("Test Category","Test Room", new ArrayList<>(),"Test Admin");
        chatroomScreen.start(new Stage());
    }

    public void handleTestChatroomListingsButtonAction(ActionEvent actionEvent) {
        if (SessionManager.getInstance().getChatroomWelcomeScreenController() != null) return;
        ChatroomWelcomeScreen chatroomWelcomeScreen = new ChatroomWelcomeScreen();
        chatroomWelcomeScreen.start(new Stage());
    }
}
