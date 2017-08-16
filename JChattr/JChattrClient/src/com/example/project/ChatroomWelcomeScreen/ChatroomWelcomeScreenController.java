package com.example.project.ChatroomWelcomeScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ChatroomWelcomeScreenController {
    @FXML
    Parent root;
    @FXML
    ListView categories_listview;
    @FXML
    ListView chatrooms_listview;

    public void initialize() {

    }

    public void shutdown() {
        Stage stage = (Stage) root.getScene().getWindow();
        SessionManager.getInstance().setChatroomWelcomeScreenController(null);
        stage.close();
    }

    public void handleEnterChatroomButtonAction(MouseEvent mouseEvent) {

    }

    public void handleWhosChattingButtonAction(MouseEvent mouseEvent) {

    }

    public void handleCreateChatroomButtonAction(MouseEvent mouseEvent) {

    }
}
