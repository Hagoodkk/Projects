package com.example.project.WelcomeScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class WelcomeScreen extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScreen.fxml"));
        Parent root = loader.load();
        WelcomeScreenController welcomeScreenController = loader.getController();
        SessionManager.getInstance().setWelcomeScreenController(welcomeScreenController);
        Stage welcomeScreenStage = new Stage();
        welcomeScreenStage.setTitle("JChattr");
        welcomeScreenStage.getIcons().add(new Image("images/appIcon.gif"));
        welcomeScreenStage.setOnCloseRequest(e -> welcomeScreenController.shutdown());
        welcomeScreenStage.setScene(new Scene(root));
        welcomeScreenStage.show();
    }

    public static void main(String[] args) {
        // Instantiation of connection host/port here
        String hostName = (args.length > 0) ? "10.0.0.88" : "71.62.87.47";
        int portNumber = 10007;
        SessionManager.getInstance().setConnectionInformation(hostName, portNumber);
        launch(args);
    }
}
