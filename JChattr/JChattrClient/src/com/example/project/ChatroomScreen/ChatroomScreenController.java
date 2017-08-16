package com.example.project.ChatroomScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ChatroomScreenController {
    private String chatroomName, chatroomAdmin;

    @FXML
    Parent root;
    @FXML
    TextField text_entry_field;
    @FXML
    ListView users_list_view;
    @FXML
    ListView text_list_view;

    public void initialize() {

    }

    public void initData(String chatroomName, String chatroomAdmin) {
        this.chatroomName = chatroomName;
        this.chatroomAdmin = chatroomAdmin;
    }

    public void shutdown() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
        SessionManager.getInstance().removeChatroomScreenController(chatroomName);
    }

    public void handleKeyPressedAction(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            handleSendButtonAction();
        }
    }

    public void handleSendButtonAction() {
        String message = text_entry_field.getText();
        if (message == null) return;


        text_entry_field.clear();
        text_entry_field.requestFocus();
    }
}
