package com.example.project.WelcomeScreen;

import com.example.project.PasswordSalter.PasswordSalter;
import com.example.project.SessionManager.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
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
        welcomeScreenStage.setTitle("JChattr");

        welcomeScreenStage.getIcons().add(new Image("images/appIcon.gif"));

        welcomeScreenStage.setOnCloseRequest(e -> welcomeScreenController.shutdown());

        welcomeScreenStage.setScene(new Scene(root));

        welcomeScreenStage.show();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            SessionManager.getInstance().setServerAddress("71.62.87.47");
        } else {
            SessionManager.getInstance().setServerAddress("10.0.0.88");
        }
        launch(args);
    }
}
