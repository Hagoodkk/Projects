package com.example.project.ChatroomScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ChatroomScreenController {
    private String chatroomName, chatroomAdmin;

    @FXML
    Parent root;

    public void initData(String chatroomName, String chatroomAdmin) {
        this.chatroomName = chatroomName;
        this.chatroomAdmin = chatroomAdmin;
    }

    public void shutdown() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
        SessionManager.getInstance().removeChatroomScreenController(chatroomName);
    }
}
