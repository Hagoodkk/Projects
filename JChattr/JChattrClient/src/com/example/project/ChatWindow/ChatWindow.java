package com.example.project.ChatWindow;

import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatWindow {
    private SessionManager sessionManager = SessionManager.getInstance();

    private String username;
    private String recipientDisplayName;

    public void start() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
            Parent root = loader.load();
            ChatWindowController controller = loader.getController();
            sessionManager.addChatWindowController(username, recipientDisplayName, controller);
            controller.initData(username, recipientDisplayName);
            Stage chatWindowStage = new Stage();
            chatWindowStage.setTitle("Chat with " + recipientDisplayName);
            chatWindowStage.getIcons().add(new Image("images/penguin1.png"));

            chatWindowStage.setScene(new Scene(root, 502, 344));
            chatWindowStage.setOnCloseRequest(e -> controller.shutdown());

            chatWindowStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void initData(String username, String recipientDisplayName) {
        this.username = username;
        this.recipientDisplayName = recipientDisplayName;
    }
}
