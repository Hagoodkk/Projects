package com.example.project.CreateAccountScreen;

import com.example.project.PasswordSalter.PasswordSalter;
import com.example.project.Serializable.ServerHello;
import com.example.project.Serializable.UserCredentials;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

    private final int PORT_NUMBER = 10007;
    private final String HOST_NAME = "10.0.0.88";

    public void initialize() {
        Platform.runLater(() -> username_input.requestFocus());
    }

    public void handleCreateAccountButtonAction(ActionEvent actionEvent) {
        String username = username_input.getText();
        String password = password_input.getText();
        String password2 = password2_input.getText();

        if (isValid(username) && isValid(password) && isValid(password2)) {
            if (password.equals(password2)) {
                try {
                    Socket clientSocket = new Socket(HOST_NAME, PORT_NUMBER);
                    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

                    ServerHello serverHello = new ServerHello();
                    serverHello.setRequestUserCreation(true);
                    oos.writeObject(serverHello);
                    oos.flush();
                    serverHello = (ServerHello) ois.readObject();

                    PasswordSalter passwordSalter = new PasswordSalter();
                    String passwordSalt = passwordSalter.getRandomSalt();
                    String passwordSaltedHash = passwordSalter.getHash(password, passwordSalt);

                    UserCredentials userCredentials = new UserCredentials(username, passwordSaltedHash, passwordSalt);

                    oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    ois = new ObjectInputStream(clientSocket.getInputStream());

                    oos.writeObject(userCredentials);
                    oos.flush();

                    userCredentials = (UserCredentials) ois.readObject();
                    if (userCredentials.isRequestAccepted()) System.out.println("Account created.");
                    else System.out.println("Account creation failed.");

                    Stage currentStage = (Stage) root.getScene().getWindow();
                    currentStage.close();

                } catch (IOException ioe) {

                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                }
            }
        }
    }

    private boolean isValid(String entry) {
        if (entry.contains(" ")) return false;
        if (entry.length() == 0) return false;
        return true;
    }
}
