package com.example.project.ChatroomWelcomeScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatroomWelcomeScreen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage chatroomWelcomeScreenStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatroomWelcomeScreen.fxml"));
            Parent root = loader.load();
            ChatroomWelcomeScreenController chatroomWelcomeScreenController = loader.getController();
            SessionManager.getInstance().setChatroomWelcomeScreenController(chatroomWelcomeScreenController);
            chatroomWelcomeScreenStage.setTitle("Chat Room Listings");
            chatroomWelcomeScreenStage.getIcons().add(new Image("images/appIcon.gif"));

            chatroomWelcomeScreenStage.setScene(new Scene(root));
            chatroomWelcomeScreenStage.setOnCloseRequest(e -> chatroomWelcomeScreenController.shutdown());
            chatroomWelcomeScreenStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
