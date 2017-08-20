package com.example.project.CreateAccountScreen;

import com.example.project.SessionManager.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateAccountScreenController {
    @FXML
    TextField username_input;
    @FXML
    PasswordField password_input;
    @FXML
    PasswordField password2_input;
    @FXML
    Button createAccount_button;
    @FXML
    Parent root;

    public void initialize() {
        Platform.runLater(() -> username_input.requestFocus());
    }

    public void handleCreateAccountButtonAction() {
        String username = username_input.getText();
        String password = password_input.getText();
        String password2 = password2_input.getText();

        if (isValid(username) && isValid(password) && isValid(password2)) {
            if (password.equals(password2)) {

                Task createAccountTask = new Task<Void>() {
                    @Override public Void call() {
                        SessionManager.getInstance().getConnectionController().pingServer(0, username, password);
                        return null;
                    }
                }; new Thread(createAccountTask).start();

                Stage currentStage = (Stage) root.getScene().getWindow();
                currentStage.close();
            }
        }
    }

    private boolean isValid(String entry) {
        if (entry.contains(" ")) return false;
        if (entry.length() == 0) return false;
        return true;
    }
}