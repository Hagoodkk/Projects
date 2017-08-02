package com.example.project.BuddyListScreen;

import com.example.project.ChatWindow.ChatWindow;
import com.example.project.ChatWindow.ChatWindowController;
import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import com.example.project.WelcomeScreen.WelcomeScreenController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class BuddyListScreenController {
    @FXML
    ListView buddyListView;

    private Timer timer;

    private ObservableList<String> listViewItems;

    private SessionManager sessionManager = SessionManager.getInstance();
    private Socket clientSocket = sessionManager.getClientSocket();
    private String username = sessionManager.getUsername();
    private int ithSecond;

    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    @FXML
    public void initialize() {
        buildBuddyList(sessionManager.getBuddyList());
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
                Message serverOutbound;
                if (sessionManager.getOutgoingQueue().isEmpty()) {
                    serverOutbound = new Message(true);
                } else {
                    serverOutbound = sessionManager.getOutgoingQueue().remove();
                }
                if (ithSecond == (timerInterval/4)) {
                    serverOutbound.setBuddyListUpdate(true);
                    serverOutbound.setBuddyList(sessionManager.getBuddyList());
                    ithSecond = 0;
                } else ithSecond++;

                try {
                    toServer.writeObject(serverOutbound);
                    toServer.flush();
                    Message serverInbound = (Message) fromServer.readObject();
                    if (serverInbound.isBuddyListUpdate()) {
                        Platform.runLater(() -> updateBuddyList(serverInbound.getBuddyList()));
                    }
                    if (!serverInbound.isNullMessage()) {
                        System.out.println(serverInbound.getMessage());
                        ChatWindowController controller =
                                sessionManager.getChatWindowController(username, serverInbound.getSender());
                        if (controller != null) {
                            controller.appendText(serverInbound.getSender(), serverInbound.getMessage());
                        } else {
                            Platform.runLater(() -> {
                                   ChatWindow chatWindow = new ChatWindow();
                                   chatWindow.initData(username,
                                           serverInbound.getSender());
                                   try {
                                       chatWindow.start();
                                       ChatWindowController controller2 =
                                               sessionManager.getChatWindowController(username, serverInbound.getSender());
                                       controller2.appendText(serverInbound.getSender(), serverInbound.getMessage());
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }

                            });
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                }
            }
        }, 0, timerInterval);
    }

    private void buildBuddyList(BuddyList buddyList) {
        listViewItems = FXCollections.observableArrayList(buddyList.getCurrentlyOnline());
        buddyListView.setItems(listViewItems);
    }

    private void updateBuddyList(BuddyList buddyList) {
        String currentlySelected = null;
        if (!buddyListView.getSelectionModel().isEmpty()) {
            currentlySelected = buddyListView.getSelectionModel().getSelectedItem().toString();
        }
        listViewItems.clear();
        buddyListView.getItems().clear();
        for (String username : buddyList.getCurrentlyOnline()) {
            listViewItems.add(username);
        }
        buddyListView.setItems(listViewItems);
        if (currentlySelected != null && listViewItems.contains(currentlySelected)) {
            buddyListView.getSelectionModel().select(currentlySelected);
        }
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                String recipient = buddyListView.getSelectionModel().getSelectedItem().toString();
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
    public void shutdown() {
        timer.cancel();
        WelcomeScreenController welcomeScreenController = sessionManager.getWelcomeScreenController();
        sessionManager.closeAllChatWindows();
        welcomeScreenController.showStage();
        sessionManager.nullify();
    }

}
