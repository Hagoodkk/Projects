package com.example.project.BuddyListScreen;

import com.example.project.ChatWindow.ChatWindow;
import com.example.project.ChatWindow.ChatWindowController;
import com.example.project.Serializable.Buddy;
import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import com.example.project.WelcomeScreen.WelcomeScreenController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BuddyListScreenController {
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
    private String displayName = sessionManager.getDisplayName();
    private int ithSecond;

    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private HashMap<String, CustomTreeItem> groups;
    private HashMap<String, CustomTreeItem> onlineUsers = new HashMap<>();
    private HashMap<String, CustomTreeItem> offlineUsers = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("This is " + username + "'s debug screen.");
        BuddyList buddyList = sessionManager.getBuddyList();
        System.out.println(buddyList.getCurrentlyOnline().size());

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
        ithSecond = 0;
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
                if (ithSecond == (timerInterval / 4)) {
                    serverOutbound.setBuddyListUpdate(true);
                    serverOutbound.setBuddyList(sessionManager.getBuddyList());
                    ithSecond = 0;
                } else ithSecond++;

                try {
                    toServer.writeObject(serverOutbound);
                    toServer.flush();
                    Message serverInbound = (Message) fromServer.readObject();
                    if (serverInbound.isBuddyListUpdate()) {
                        // Idk
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
                            chatWindow.initData(username, serverInbound.getSender());
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
                } catch (IOException ioe) {
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

        if (state == 0) {
            CustomTreeItem treeItem = offlineUsers.get(sender);
            groups.get(groupName).getChildren().add(treeItem);
            groups.get("offline").getChildren().remove(treeItem);
            offlineUsers.remove(sender);
            onlineUsers.put(sender, treeItem);
        } else if (state == 1) {
            System.out.println(sender + " disconnected.");
            CustomTreeItem treeItem = onlineUsers.get(sender);
            groups.get(groupName).getChildren().remove(treeItem);
            groups.get("offline").getChildren().add(treeItem);
            onlineUsers.remove(sender);
            offlineUsers.put(sender, treeItem);
        }
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        CustomTreeItem treeItem = (CustomTreeItem) buddy_list_tree.getSelectionModel().getSelectedItem();
        if (!treeItem.isUser()) return;

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
        WelcomeScreenController welcomeScreenController = sessionManager.getWelcomeScreenController();
        sessionManager.closeAllChatWindows();
        welcomeScreenController.showStage();
        sessionManager.nullify();
    }

}
