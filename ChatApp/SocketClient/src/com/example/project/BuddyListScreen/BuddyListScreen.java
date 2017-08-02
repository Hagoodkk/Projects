package com.example.project.BuddyListScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        buddyListStage.setTitle("Buddy List" + " (" + sessionManager.getUsername() + ")");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double height = primaryScreenBounds.getHeight();
        double width = primaryScreenBounds.getWidth() / 8;
        buddyListStage.setScene(new Scene(root, width, height));
        buddyListStage.setX(primaryScreenBounds.getMinX());
        buddyListStage.setY(primaryScreenBounds.getMinY());
        buddyListStage.setOnCloseRequest(e -> buddyListScreenController.shutdown());
        buddyListStage.setMinHeight(height/4);
        buddyListStage.setMinWidth(width/2);
        buddyListStage.show();
    }
}
