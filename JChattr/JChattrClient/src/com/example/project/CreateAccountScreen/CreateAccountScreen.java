package com.example.project.CreateAccountScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CreateAccountScreen extends Application {
    private SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAccountScreen.fxml"));
        Parent root = loader.load();
        Stage createAccountScreen = new Stage();
        createAccountScreen.setTitle("Create Account");
        createAccountScreen.getIcons().add(new Image("images/appIcon.gif"));

        createAccountScreen.setScene(new Scene(root, 300, 275));
        createAccountScreen.setMinHeight(275);
        createAccountScreen.setMinWidth(300);
        createAccountScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
