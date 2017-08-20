package com.example.project.ChatroomWelcomeScreen;

import com.example.project.ChatroomScreen.ChatroomScreen;
import com.example.project.Serializable.Message;
import com.example.project.SessionManager.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ChatroomWelcomeScreenController {
    @FXML
    Parent root;
    @FXML
    ListView categories_listview;
    @FXML
    ListView chatrooms_listview;
    @FXML
    ListView whos_chatting_list_view;

    private HashMap<String, ArrayList<String>> categories;
    private HashMap<String, ArrayList<String>> users;

    private ObservableList<String> categoryList;
    private ObservableList<String> chatroomList;
    private ObservableList<String> whosChatting;

    private String currentCategory;

    public void initialize() {
        whosChatting = FXCollections.observableArrayList(new ArrayList<>());
        whos_chatting_list_view.setItems(whosChatting);

        Message message = new Message(true);
        message.setChatroomListingsRequest(true);
        SessionManager.getInstance().addOutgoingMessage(message);
    }

    public void shutdown() {

        Stage stage = (Stage) root.getScene().getWindow();
        SessionManager.getInstance().setChatroomWelcomeScreenController(null);
        stage.close();
    }

    public void fillCategories(HashMap<String, ArrayList<String>> categories, HashMap<String, ArrayList<String>> users) {
        this.categories = categories;
        this.users = users;

        if (categoryList == null || chatroomList == null) {
            this.categoryList = FXCollections.observableArrayList(new ArrayList<>());
            this.categoryList.addAll(categories.keySet());
            categories_listview.setItems(categoryList);
            categories_listview.getSelectionModel().select(0);

            this.chatroomList = FXCollections.observableArrayList(new ArrayList<>());
            chatrooms_listview.setItems(chatroomList);
            String currentlySelected = categories_listview.getSelectionModel().getSelectedItem().toString();
            chatroomList.addAll(categories.get(currentlySelected));
            this.currentCategory = categoryList.get(0);
        } else {
            String categoryClicked = categories_listview.getSelectionModel().getSelectedItem().toString();
            if (categoryClicked == null) return;
            chatroomList.clear();
            chatroomList.addAll(categories.get(categoryClicked));
            this.currentCategory = categoryClicked;
        }
    }

    public void handleEnterChatroomButtonAction(MouseEvent mouseEvent) {
        if (chatrooms_listview.getSelectionModel().getSelectedItem() != null) {
            String chatroomSelected = chatrooms_listview.getSelectionModel().getSelectedItem().toString();
            if (SessionManager.getInstance().getChatroomController() != null) {
                if (SessionManager.getInstance().getChatroomController().getChatroomName().equals(chatroomSelected)) {
                    SessionManager.getInstance().getChatroomController().focusWindow();
                    return;
                }
                SessionManager.getInstance().getChatroomController().shutdown();
            }
            Message message = new Message(true);
            message.setEnteredChatroom(true);
            message.setSenderDisplayName(SessionManager.getInstance().getDisplayName());
            message.setChatroomCategory(currentCategory);
            message.setChatroomName(chatroomSelected);
            SessionManager.getInstance().addOutgoingMessage(message);
        }
    }


    public void handleCreateChatroomButtonAction(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Create chatroom");

            Label groupLabel = new Label("Group: ");
            Label nameLabel = new Label("Chatroom Name: ");

            ChoiceBox<String> groupPicker = new ChoiceBox<>();
            groupPicker.setItems(FXCollections.observableArrayList(categoryList));
            groupPicker.getSelectionModel().select(0);

            TextField chatroomNameText = new TextField();

            GridPane grid = new GridPane();
            grid.add(groupLabel, 1, 1);
            grid.add(groupPicker, 2, 1);
            grid.add(nameLabel, 1, 2);
            grid.add(chatroomNameText, 2, 2);
            dialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

            dialog.setResultConverter(b -> {
                if (b == buttonTypeOk) {
                    return new Pair<>(groupPicker.getValue(), chatroomNameText.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            String chatroomName, categoryPicked;
            if (result.isPresent()) {
                categoryPicked = result.get().getKey();
                chatroomName = result.get().getValue();
            } else {
                chatroomName = null;
                categoryPicked = null;
            }

            if (chatroomName == null || chatroomName.length() == 0) return;

            Message message = new Message(true);
            message.setChatroomCreateRequest(true);
            message.setSenderDisplayName(SessionManager.getInstance().getDisplayName());
            message.setChatroomCategory(categoryPicked);
            message.setChatroomName(chatroomName);
            SessionManager.getInstance().addOutgoingMessage(message);

            if (SessionManager.getInstance().getChatroomController() != null) SessionManager.getInstance().getChatroomController().shutdown();
            ChatroomScreen chatroomScreen = new ChatroomScreen();
            ArrayList<String> users = new ArrayList<>();
            users.add(SessionManager.getInstance().getDisplayName());
            chatroomScreen.initData(categoryPicked, chatroomName, users, SessionManager.getInstance().getDisplayName());
            chatroomScreen.start(new Stage());
        }
    }

    public void handleCategoryClickedAction(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            if (mouseEvent.getClickCount() == 2) {
                this.initialize();
            }
        }
    }

    public void handleChatroomsClickedAction(MouseEvent mouseEvent) {
        if (chatrooms_listview.getSelectionModel().getSelectedItem() != null && mouseEvent.getButton() == MouseButton.PRIMARY) {
            String chatroomName = chatrooms_listview.getSelectionModel().getSelectedItem().toString();
            whosChatting.clear();
            whosChatting.addAll(users.get(chatroomName));
            if (mouseEvent.getClickCount() == 2) {
                handleEnterChatroomButtonAction(mouseEvent);
            }
        }
    }

    public void handleWhosChattingClickedAction(MouseEvent mouseEvent) {

    }
}
