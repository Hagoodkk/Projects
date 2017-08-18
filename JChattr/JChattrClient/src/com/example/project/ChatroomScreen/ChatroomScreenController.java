package com.example.project.ChatroomScreen;

import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;

public class ChatroomScreenController {
    private String chatroomCategory, chatroomName, chatroomAdmin;
    private ArrayList<String> chatroomUsers;

    @FXML
    Parent root;
    @FXML
    TextField text_entry_field;
    @FXML
    ListView users_list_view;
    @FXML
    ListView text_list_view;

    private ObservableList<String> users;

    public void initializeList() {
        users = FXCollections.observableArrayList(chatroomUsers);
        users_list_view.setItems(users);
    }

    public void initData(String chatroomCategory, String chatroomName, ArrayList<String> chatroomUsers, String chatroomAdmin) {
        this.chatroomCategory = chatroomCategory;
        this.chatroomName = chatroomName;
        this.chatroomUsers = chatroomUsers;
        this.chatroomAdmin = chatroomAdmin;
    }

    public void shutdown() {
        Message message = new Message(true);
        message.setSenderDisplayName(SessionManager.getInstance().getDisplayName());
        message.setLeftChatroom(true);
        message.setChatroomCategoryAndName(new Pair<>(chatroomCategory, chatroomName));
        SessionManager.getInstance().addOutgoingMessage(message);

        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
        SessionManager.getInstance().removeChatroomScreenController();
    }

    public void handleKeyPressedAction(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            handleSendButtonAction();
        }
    }

    public void addUser(String displayName) {
        users.add(displayName);
    }

    public void removeUser(String displayName) {
        users.remove(displayName);
    }

    public void handleSendButtonAction() {
        String message = text_entry_field.getText();
        if (message == null) return;


        text_entry_field.clear();
        text_entry_field.requestFocus();
    }
}
