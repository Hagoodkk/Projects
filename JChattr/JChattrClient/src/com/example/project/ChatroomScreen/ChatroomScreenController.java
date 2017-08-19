package com.example.project.ChatroomScreen;

import com.example.project.ChatWindow.ChatWindow;
import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;

import static javax.swing.SwingConstants.CENTER;

public class ChatroomScreenController {
    private String chatroomCategory, chatroomName, chatroomAdmin;
    private ArrayList<String> chatroomUsers;

    private String lastSenderDisplayName;

    @FXML
    Parent root;
    @FXML
    TextField text_entry_field;
    @FXML
    ListView users_list_view;
    @FXML
    ListView text_list_view;

    private ObservableList<String> users;
    private ObservableList<HBox> messages;

    public void initializeList() {
        users = FXCollections.observableArrayList(chatroomUsers);
        users_list_view.setItems(users);
        messages = FXCollections.observableArrayList(new ArrayList<>());
        text_list_view.setItems(messages);

        HBox hbox = new HBox();
        hbox.setPrefWidth(1);
        hbox.setAlignment(Pos.CENTER);
        Label welcomeLabel = new Label("Welcome to " + chatroomName);
        welcomeLabel.setStyle("-fx-font-style: italic; -fx-font-size: 80%");
        welcomeLabel.setWrapText(true);
        hbox.getChildren().add(welcomeLabel);
        messages.add(hbox);
    }

    public void initData(String chatroomCategory, String chatroomName, ArrayList<String> chatroomUsers, String chatroomAdmin) {
        this.chatroomCategory = chatroomCategory;
        this.chatroomName = chatroomName;
        this.chatroomUsers = chatroomUsers;
        this.chatroomAdmin = chatroomAdmin;
    }

    public String getChatroomName() {
        return this.chatroomName;
    }

    public void focusWindow() {
        Stage stage = (Stage) root.getScene().getWindow();
        Platform.runLater(() -> stage.requestFocus());
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

    public void appendMessage(String senderDisplayName, String message) {
        if (lastSenderDisplayName != null && lastSenderDisplayName.equals(senderDisplayName)) {
            Label lastLabel = (Label) messages.get(messages.size()-1).getChildren().get(0);
            lastLabel.setText(lastLabel.getText() + "\n" + message);
        }
        else{
            Label label = new Label(senderDisplayName + "\n" + message);
            label.setWrapText(true);
            HBox hbox = new HBox(label);
            hbox.setPrefWidth(1);
            messages.add(hbox);
        }
        lastSenderDisplayName = senderDisplayName;
    }

    public void appendUpdateMessage(String displayName, String event) {
        Label label = new Label(displayName + " " + event);
        label.setWrapText(true);
        label.setStyle("-fx-font-style: italic; -fx-font-size: 80%");
        HBox hbox = new HBox(label);
        hbox.setPrefWidth(1);
        hbox.setAlignment(Pos.CENTER);
        messages.add(hbox);
        lastSenderDisplayName = null;
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

        Message chatMessage = new Message(true);
        chatMessage.setCarryingChatroomMessage(true);
        chatMessage.setChatroomCategoryAndName(new Pair<>(chatroomCategory, chatroomName));
        chatMessage.setChatroomMessage(message);
        chatMessage.setSenderDisplayName(SessionManager.getInstance().getDisplayName());
        SessionManager.getInstance().addOutgoingMessage(chatMessage);

        text_entry_field.clear();
        text_entry_field.requestFocus();
    }

    public void handleUserClickedAction(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY
            && mouseEvent.getClickCount() == 2
                && users_list_view.getSelectionModel().getSelectedItem() != null) {
            String recipient = users_list_view.getSelectionModel().getSelectedItem().toString();
            if (SessionManager.getInstance().getChatWindowController(
                    SessionManager.getInstance().getUsername(), recipient) != null) {
                SessionManager.getInstance().getChatWindowController(SessionManager.getInstance().getUsername(), recipient).requestFocus();
            } else {
                ChatWindow chatWindow = new ChatWindow();
                chatWindow.initData(SessionManager.getInstance().getUsername(), recipient);
                try {
                    chatWindow.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
