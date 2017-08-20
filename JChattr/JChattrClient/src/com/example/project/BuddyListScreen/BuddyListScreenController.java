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

    private SessionManager sessionManager = SessionManager.getInstance();
    private String username = sessionManager.getUsername();

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
    }

    public void addUserToBuddyList(String buddyUsername, String buddyDisplayName, String buddyGroupName, boolean buddyOnline) {
        if (groups.get(buddyGroupName) == null) {
            CustomTreeItem groupItem = new CustomTreeItem(new HBox(), buddyGroupName, true, false);
            groups.get("root").getChildren().add(0, groupItem);
            groups.put(buddyGroupName, groupItem);
        }

        CustomTreeItem treeItem = new CustomTreeItem(new HBox(), buddyDisplayName, null);

        if (buddyOnline) {
            offlineUsers.put(buddyUsername, treeItem);
            groups.get("offline").getChildren().add(treeItem);
        } else {
            onlineUsers.put(buddyUsername, treeItem);
            groups.get(buddyGroupName).getChildren().add(treeItem);
        }
        sessionManager.getBuddyList().addBuddy(new Buddy(buddyUsername, buddyDisplayName, buddyGroupName));
    }
    public void deleteUserFromBuddyList(String buddyUsername, String buddyGroupName) {
        CustomTreeItem treeItem;
        if ((treeItem = offlineUsers.get(buddyUsername)) != null) {
            offlineUsers.remove(buddyUsername);
            groups.get("offline").getChildren().remove(treeItem);
        } else if ((treeItem = onlineUsers.get(buddyUsername)) != null) {
            onlineUsers.remove(treeItem);
            groups.get(buddyGroupName).getChildren().remove(treeItem);
        }
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

    public void updateBuddyList(String sender, int state) {
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
        if (SessionManager.getInstance().getChatroomController() != null) {
            SessionManager.getInstance().getChatroomController().shutdown();
        }
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


    public void handleTestChatroomListingsButtonAction(ActionEvent actionEvent) {
        if (SessionManager.getInstance().getChatroomWelcomeScreenController() != null) return;
        ChatroomWelcomeScreen chatroomWelcomeScreen = new ChatroomWelcomeScreen();
        chatroomWelcomeScreen.start(new Stage());
    }
}
