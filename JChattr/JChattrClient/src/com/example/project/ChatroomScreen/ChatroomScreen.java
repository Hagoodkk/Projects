package com.example.project.ChatroomScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatroomScreen extends Application {
    private String chatroomName;
    private String chatroomAdmin;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage chatroomWindowStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatroomScreen.fxml"));
            Parent root = loader.load();
            ChatroomScreenController chatroomScreenController = loader.getController();
            SessionManager.getInstance().addChatroomController(chatroomName, chatroomScreenController);
            chatroomScreenController.initData(chatroomName, chatroomAdmin);
            chatroomWindowStage.setTitle(chatroomName);
            chatroomWindowStage.getIcons().add(new Image("images/appIcon.gif"));

            chatroomWindowStage.setScene(new Scene(root));
            chatroomWindowStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void initData(String chatroomName, String chatroomAdmin) {
        this.chatroomName = chatroomName;
        this.chatroomAdmin = chatroomAdmin;
    }
}
