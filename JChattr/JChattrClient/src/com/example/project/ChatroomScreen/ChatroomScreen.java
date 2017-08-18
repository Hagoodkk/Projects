package com.example.project.ChatroomScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ChatroomScreen extends Application {
    private String chatroomCategory;
    private String chatroomName;
    private ArrayList<String> chatroomUsers;
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
            chatroomScreenController.initData(chatroomCategory, chatroomName, chatroomUsers, chatroomAdmin);
            chatroomScreenController.initializeList();
            SessionManager.getInstance().setChatroomScreenController(chatroomScreenController);
            chatroomWindowStage.setTitle(chatroomName);
            chatroomWindowStage.getIcons().add(new Image("images/appIcon.gif"));
            chatroomWindowStage.setOnCloseRequest(e -> chatroomScreenController.shutdown());

            chatroomWindowStage.setScene(new Scene(root));
            chatroomWindowStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void initData(String chatroomCategory, String chatroomName, ArrayList<String> chatroomUsers, String chatroomAdmin) {
        this.chatroomCategory = chatroomCategory;
        this.chatroomName = chatroomName;
        this.chatroomUsers = chatroomUsers;
        this.chatroomAdmin = chatroomAdmin;
    }
}
