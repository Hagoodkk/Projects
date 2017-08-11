package com.example.project.ChatWindow;

import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ChatWindow {
    private SessionManager sessionManager = SessionManager.getInstance();

    private String username;
    private String recipient;

    public void start() throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
        Parent root = loader.load();
        ChatWindowController controller = loader.getController();
        sessionManager.addChatWindowController(username, recipient, controller);
        controller.initData(username, recipient);
        Stage chatWindowStage = new Stage();
        chatWindowStage.setTitle("Chat with " + recipient);
        chatWindowStage.getIcons().add(new Image("images/penguin1.png"));

        chatWindowStage.setScene(new Scene(root, 502, 344));
        chatWindowStage.setOnCloseRequest(e -> controller.shutdown());

        chatWindowStage.show();
    }

    public void initData(String username, String recipient) {
        this.username = username;
        this.recipient = recipient;
    }
}
