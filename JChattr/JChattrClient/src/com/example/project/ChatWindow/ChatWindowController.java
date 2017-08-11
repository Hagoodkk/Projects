package com.example.project.ChatWindow;

import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import java.sql.Time;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ChatWindowController {
    @FXML
    TextField input_field;
    @FXML
    ListView list_view;
    @FXML
    Button send_button;
    @FXML
    Parent root;

    private SessionManager sessionManager = SessionManager.getInstance();
    private String username;
    private String recipient;

    private boolean firstMessage = true;
    private boolean sentLastMessage = false;

    private ObservableList<HBox> chatBox;

    public void initialize() {
        chatBox = FXCollections.observableArrayList(new ArrayList<>());
        list_view.setItems(chatBox);
    }

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

        Label label = new Label();
        label.setWrapText(true);

        HBox hbox = new HBox();
        hbox.setPrefWidth(1);
        hbox.setAlignment(Pos.TOP_RIGHT);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(0, 15, 0, 0));

        String currentTime = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString();
        int currentHour = Integer.parseInt(currentTime.substring(0, currentTime.indexOf(":")));
        currentHour = currentHour % 12;
        currentTime = String.valueOf(currentHour) + currentTime.substring(currentTime.indexOf(":"), currentTime.length());

        Label timeLabel = new Label();
        timeLabel.setText(currentTime);
        timeLabel.setStyle("-fx-font-style: italic; -fx-font-size: 80%");
        timeLabel.setWrapText(true);
        timeLabel.setAlignment(Pos.TOP_CENTER);

        HBox timeHBox = new HBox();
        timeHBox.setAlignment(Pos.TOP_CENTER);
        timeHBox.getChildren().add(timeLabel);

        if (!sentLastMessage || firstMessage) {
            Image image = new Image("images/penguin1.png");
            ImageView iconPicture = new ImageView(image);
            iconPicture.setFitHeight(32);
            iconPicture.setFitWidth(32);

            label.setText(message);
            vbox.getChildren().add(label);
            vbox.setAlignment(Pos.TOP_RIGHT);
            hbox.getChildren().addAll(vbox, iconPicture);

            chatBox.add(timeHBox);
            chatBox.add(hbox);

        } else {
            Label newLabel = new Label();
            newLabel.setWrapText(true);
            newLabel.setText(message);
            VBox oldVBox = (VBox) chatBox.get(chatBox.size()-1).getChildren().get(0);
            oldVBox.getChildren().add(newLabel);
        }

        Platform.runLater(() -> list_view.scrollTo(chatBox.size()-1));

        input_field.clear();
        input_field.requestFocus();
        sentLastMessage = true;
        firstMessage = false;

        Media media = new Media(getClass().getClassLoader().getResource("sounds/266455__infinitelifespan__notify.wav").toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public void initData(String username, String recipient) {
        this.username = username;
        this.recipient = recipient;
    }

    public void appendText(String sender, String message) {
        if (username.equals(sender)) return;

        String currentTime = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString();
        int currentHour = Integer.parseInt(currentTime.substring(0, currentTime.indexOf(":")));
        currentHour = currentHour % 12;
        currentTime = String.valueOf(currentHour) + currentTime.substring(currentTime.indexOf(":"), currentTime.length());

        Label timeLabel = new Label();
        timeLabel.setText(currentTime);
        timeLabel.setStyle("-fx-font-style: italic; -fx-font-size: 80%");
        timeLabel.setWrapText(true);
        timeLabel.setAlignment(Pos.TOP_CENTER);

        HBox timeHBox = new HBox();
        timeHBox.setAlignment(Pos.TOP_CENTER);
        timeHBox.getChildren().add(timeLabel);

        if (sentLastMessage || firstMessage) {
            Image image = new Image("images/penguin2.png");
            ImageView iconPicture = new ImageView(image);
            iconPicture.setFitHeight(32);
            iconPicture.setFitWidth(32);

            Label label = new Label();
            label.setWrapText(true);
            label.setText(message);

            HBox hbox = new HBox();
            hbox.setPrefWidth(1);
            hbox.setAlignment(Pos.TOP_LEFT);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(0, 0, 0, 15));
            vbox.setAlignment(Pos.TOP_LEFT);
            vbox.getChildren().add(label);

            hbox.getChildren().addAll(iconPicture, vbox);
            Platform.runLater(() -> {
                chatBox.add(timeHBox);
                chatBox.add(hbox);
            });
        } else {
            Label newLabel = new Label();
            newLabel.setWrapText(true);
            newLabel.setText(message);
            Platform.runLater(() -> {
                VBox oldVBox = (VBox) chatBox.get(chatBox.size()-1).getChildren().get(1);
                oldVBox.getChildren().add(newLabel);
            });
        }

        Platform.runLater(() -> list_view.scrollTo(chatBox.size()-1));

        sentLastMessage = false;
        firstMessage = false;

        Media media = new Media(getClass().getClassLoader().getResource("sounds/380482__josepharaoh99__chime-notification.wav").toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public void shutdown() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();
        sessionManager.removeChatWindowController(username, recipient);
    }

    public void requestFocus() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        Platform.runLater(() -> currentStage.requestFocus());
    }

}
