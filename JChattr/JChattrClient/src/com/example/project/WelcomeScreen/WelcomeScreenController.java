package com.example.project.WelcomeScreen;

import com.example.project.BuddyListScreen.BuddyListScreen;
import com.example.project.CreateAccountScreen.CreateAccountScreen;
import com.example.project.PasswordSalter.PasswordSalter;
import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.ServerHello;
import com.example.project.Serializable.UserCredentials;
import com.example.project.SessionManager.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class WelcomeScreenController {
    @FXML
    Parent root;
    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private ImageView welcome_image;

    @FXML
    public void initialize() {
        welcome_image.setImage(new Image("images/appIcon.gif"));
        Platform.runLater(() -> username_field.requestFocus());
    }

    public void handleSignInButtonAction() {
        String username = username_field.getText().toLowerCase();
        String password = password_field.getText();

        Task loginTask = new Task<Void>() {
            @Override public Void call() {
                SessionManager.getInstance().getConnectionController().pingServer(1, username, password);
                return null;
            }
        }; new Thread(loginTask).start();
    }

    public void handleCreateAccountButtonAction() {
        try {
            new CreateAccountScreen().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBuddyList() {
        hideStage();
        username_field.clear();
        password_field.clear();
        username_field.requestFocus();

        try {
            new BuddyListScreen().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            handleSignInButtonAction();
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
    public void shutdown() {

    }
}
