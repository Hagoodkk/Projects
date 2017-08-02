package com.example.project.WelcomeScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WelcomeScreen extends Application {
    private SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScreen.fxml"));
        Parent root = loader.load();
        WelcomeScreenController welcomeScreenController = loader.getController();
        sessionManager.setWelcomeScreenController(welcomeScreenController);
        Stage welcomeScreenStage = new Stage();
        welcomeScreenStage.setTitle("Chat Program");
        welcomeScreenStage.setScene(new Scene(root, 300, 275));
        welcomeScreenStage.setMinHeight(275);
        welcomeScreenStage.setMinWidth(300);
        welcomeScreenStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
