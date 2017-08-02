package com.example.project.WelcomeScreen;

import com.example.project.BuddyListScreen.BuddyListScreen;
import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.UserCredentials;
import com.example.project.SessionManager.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class WelcomeScreenController {
    @FXML
    Parent root;
    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;

    private final int PORT_NUMBER = 10007;
    private final String HOST_NAME = "10.0.0.88";

    SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void initialize() {
        Platform.runLater(() -> username_field.requestFocus());
    }

    public void handleSignInButtonAction(ActionEvent actionEvent) {
        String username = username_field.getText();
        sessionManager.setUsername(username);

        try {
            Socket clientSocket = new Socket(HOST_NAME, PORT_NUMBER);
            sessionManager.setClientSocket(clientSocket);
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            UserCredentials userCredentials = new UserCredentials(sessionManager.getUsername(), "");
            oos.writeObject(userCredentials);
            BuddyList buddyList = (BuddyList) ois.readObject();
            sessionManager.setBuddyList(buddyList);
            showBuddyList();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public void showBuddyList() {
        hideStage();
        username_field.clear();
        password_field.clear();
        username_field.requestFocus();

        BuddyListScreen buddyListScreen = new BuddyListScreen();
        try {
            buddyListScreen.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            handleSignInButtonAction(new ActionEvent());
        }
    }

    public void showStage() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.show();
    }

    public void hideStage() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.hide();
    }
}
