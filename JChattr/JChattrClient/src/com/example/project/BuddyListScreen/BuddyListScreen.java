package com.example.project.BuddyListScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class BuddyListScreen {
    private SessionManager sessionManager = SessionManager.getInstance();

    public void start() throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BuddyListScreen.fxml"));
        Parent root = loader.load();
        BuddyListScreenController buddyListScreenController = loader.getController();
        sessionManager.setBuddyListScreenController(buddyListScreenController);
        Stage buddyListStage = new Stage();
        buddyListStage.setTitle("Buddy List" + " (" + sessionManager.getDisplayName() + ")");
        buddyListStage.getIcons().add(new Image("images/appIcon.gif"));

        buddyListStage.setScene(new Scene(root));
        buddyListStage.setOnCloseRequest(e -> buddyListScreenController.shutdown());
        buddyListStage.show();
    }
}
