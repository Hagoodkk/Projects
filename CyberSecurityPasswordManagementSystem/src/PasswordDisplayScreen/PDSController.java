package PasswordDisplayScreen;

import DatabaseManager.DatabaseManager;
import Encryptor.Encryptor;
import SessionManager.SessionManager;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import javax.swing.plaf.nimbus.State;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Kyle on 4/15/2017.
 */

public class PDSController {
    @FXML
    private VBox passwordBox = new VBox();
    @FXML
    private ListView<String> passwordList = new ListView<>();
    @FXML
    private Parent root;

    private DatabaseManager dbm = DatabaseManager.getInstance();
    SessionManager sessionManager = SessionManager.getInstance();
    Encryptor encryptor = Encryptor.getInstance();
    private String username = sessionManager.getUsername();
    private Stage lastStage = sessionManager.getLastStage();

    private Statement statement;
    private ResultSet resultSet;

    private ObservableList<String> passwords = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            statement = dbm.getConnection().createStatement();
            String query = "SELECT NAME, PW FROM " + username;
            resultSet = statement.executeQuery(query);
            passwords.clear();
            passwordBox.getChildren().clear();
            passwordBox.getChildren().add(passwordList);

            String currentString;
            String currentName;
            while (resultSet.next()) {
                currentName = resultSet.getString("NAME");
                currentString = encryptor.getEncryptionTool().decrypt(resultSet.getString("PW"));
                if (currentString != null) {
                    passwords.add(currentName + " , " + currentString);
                }
            }

            passwordList.setItems(passwords);
            for (String password : passwords) {
                System.out.println(password);
            }

            passwordList.setCellFactory(new Callback<ListView<String>,
                ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> list) {
                        return new ListViewCell();
                    }
                }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAddPasswordButton(ActionEvent actionEvent) {
        Pair<String, String> entry = getNewPasswordPrompt(null, null);

        if (entry != null) {
            String encryptedEntry = encryptor.getEncryptionTool().encrypt(entry.getValue());
            System.out.println(encryptedEntry);
            try {
                statement = dbm.getConnection().createStatement();
                String newPassUpdate = "INSERT INTO " + username + " (NAME,PW) VALUES ('" + entry.getKey() + "', '" + encryptedEntry + "')";
                statement.executeUpdate(newPassUpdate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Bring up window saying it wasn't added
        }
        initialize();
    }

    public void handleDeletePasswordButton(ActionEvent actionEvent) {
        String currentPw = passwordList.getSelectionModel().getSelectedItem();
        try {
            statement = dbm.getConnection().createStatement();
            String newPassUpdate = "DELETE FROM " + username + " WHERE NAME = '" + currentPw.substring(0, currentPw.indexOf(' ')) + "'";
            System.out.print(newPassUpdate);
            statement.executeUpdate(newPassUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize();
    }

    public void handleLogoutButton(ActionEvent actionEvent)  {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.hide();

        SessionManager session1 = SessionManager.getInstance();
        session1.getLastStage().show();

    }

    Pair<String, String> getNewPasswordPrompt(String currentIdentifier, String currentPassword) {

        Dialog<Pair <String, String>> dialog = new Dialog();
        dialog.setTitle("Add New Password");
        dialog.setHeaderText("Enter an identifier, and your new password.");
        TextField identifier = new TextField();
        identifier.setPromptText("Identifier");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.add((new Text("Identifier")), 0, 0);
        gridPane.add((new Text("Password:")), 0, 1);
        gridPane.add(identifier, 1, 0);
        gridPane.add(password, 1, 1);

        if (currentIdentifier != null) identifier.setText(currentIdentifier);
        if (currentPassword != null) password.setText(currentPassword);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> identifier.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(identifier.getText(), password.getText());
            }
            return null;
        });

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        identifier.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                okButton.setDisable(identifier.getText().isEmpty() || password.getText().isEmpty());
            }
        });

        password.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                okButton.setDisable(identifier.getText().isEmpty() || password.getText().isEmpty());
            }
        });

        Optional<Pair <String, String>> result = dialog.showAndWait();

        try {
            return result.get();
        } catch (Exception e) {
            return null;
        }
    }

    public void handleEditPasswordButton(ActionEvent actionEvent) {
        String currentPw = passwordList.getSelectionModel().getSelectedItem();
        String currentIdentifier = currentPw.substring(0, currentPw.indexOf(' '));
        String currentPassword = currentPw.substring(currentPw.indexOf(' ')+3, currentPw.length());
        System.out.println(currentIdentifier);
        System.out.println(currentPassword);
        Pair<String, String> result = getNewPasswordPrompt(currentIdentifier, currentPassword);
        try {
            statement = dbm.getConnection().createStatement();
            String newPassUpdate = "DELETE FROM " + username + " WHERE NAME = '" + currentIdentifier + "'";
            System.out.print(newPassUpdate);
            statement.executeUpdate(newPassUpdate);
            String encryptedEntry = encryptor.getEncryptionTool().encrypt(result.getValue());
            String entryUpdate = "INSERT INTO " + username + " (NAME,PW) VALUES ('" + result.getKey() + "', '" + encryptedEntry + "')";
            statement.executeUpdate(entryUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize();

    }

    public void handleEditMasterPasswordButton(ActionEvent actionEvent) {
        Dialog<Pair <String, String>> dialog = new Dialog();
        dialog.setTitle("Edit Master Password");
        dialog.setHeaderText("Enter your current password, and your new password.");
        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("Current Password");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.add((new Text("Current Password:")), 0, 0);
        gridPane.add((new Text("New Password:")), 0, 1);
        gridPane.add(currentPassword, 1, 0);
        gridPane.add(newPassword, 1, 1);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> currentPassword.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(currentPassword.getText(), newPassword.getText());
            }
            return null;
        });

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        currentPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                okButton.setDisable(currentPassword.getText().isEmpty() || newPassword.getText().isEmpty());
            }
        });

        newPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                okButton.setDisable(currentPassword.getText().isEmpty() || newPassword.getText().isEmpty());
            }
        });

        Optional<Pair <String, String>> result = dialog.showAndWait();

        // Edit Master Password with result here
        Pair<String, String> pair = result.get();
        String currentMasterPass = pair.getKey();
        String newMasterPass = pair.getValue();

        // Check if username exists in database
        try {
            statement = dbm.getConnection().createStatement();
            String queryString = "SELECT PASSWORD FROM " + username;
            resultSet = statement.executeQuery(queryString);
            String storedPassword = null;
            if (resultSet.next()) storedPassword = resultSet.getString("password");
            storedPassword = encryptor.getEncryptionTool().decrypt(storedPassword);
            if (currentMasterPass.equals(storedPassword)) {
                System.out.println("Authenticated");
                String encryptedNewPass = encryptor.getEncryptionTool().encrypt(newMasterPass);
                // Authenticated, do something
                String updatePasswordCommand = "UPDATE " + username + " SET PASSWORD = '" + encryptedNewPass + "'";
                statement.executeUpdate(updatePasswordCommand);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Password Change Authenticated");
                alert.setContentText("Master password was successfully changed.");
                alert.showAndWait();
            } else {
                System.out.println("Not Authenticated");
                // Not Authenticated, throw error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Incorrect Password");
                alert.setContentText("Master password was not able to be changed.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Incorrect Password");
            alert.setContentText("Master password was not able to be changed.");
            alert.showAndWait();
        }
    }

    public void handleShowDatabaseButton(ActionEvent actionEvent) {
        try {
            statement = dbm.getConnection().createStatement();
            String databaseShow = "SELECT * FROM " + username;
            resultSet = statement.executeQuery(databaseShow);
            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            while (resultSet.next()) {
                for (int i = 1; i <= resultSetMD.getColumnCount(); i++) {
                    if (i > 1) System.out.print(", ");
                    System.out.print(resultSet.getString(i) + " " + resultSetMD.getColumnName(i));
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}
