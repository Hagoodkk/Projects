package com.example.project.ChatWindow;

import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ChatWindowController {
    @FXML
    TextField input_field;
    @FXML
    TextArea text_field;
    @FXML
    Button send_button;
    @FXML
    Parent root;

    private SessionManager sessionManager = SessionManager.getInstance();
    private String username;
    private String recipient;
    private boolean firstMessage = true;
    private boolean sentLastMessage = false;

    public void handleMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            sendMessage();
        }
    }

    public void handleKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    public void sendMessage() {
        String message = input_field.getText();
        if (message.length() == 0) return;
        Message outgoingMessage = new Message(username, recipient, message);
        sessionManager.addOutgoingMessage(outgoingMessage);
        text_field.appendText("\n");
        if (!sentLastMessage || firstMessage) text_field.appendText(username + "\n");
        text_field.appendText(message);
        input_field.clear();
        input_field.requestFocus();
        sentLastMessage = true;
        firstMessage = false;
    }

    public void initData(String username, String recipient) {
        this.username = username;
        this.recipient = recipient;
    }

    public void appendText(String sender, String message) {
        if (username.equals(sender)) return;
        text_field.appendText("\n");
        if (sentLastMessage || firstMessage) text_field.appendText(sender + "\n");
        text_field.appendText(message);
        sentLastMessage = false;
        firstMessage = false;
    }

    public void shutdown() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
        sessionManager.removeChatWindowController(username, recipient);
    }

}
