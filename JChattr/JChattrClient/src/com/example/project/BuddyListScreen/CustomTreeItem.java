package com.example.project.BuddyListScreen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CustomTreeItem extends TreeItem {
    private ImageView buddyIcon;
    private Label label;
    private String buddyDisplayName;
    private boolean recentlyLoggedOff, recentlyLoggedOn;
    private boolean isUser;

    public CustomTreeItem(HBox hbox, String labelText, boolean isGroup, boolean isRoot) {
        super(hbox);
        this.isUser = false;
        this.label = new Label(labelText);
        if (isRoot) {
            hbox.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 150%");
        } else if (isGroup) {
            label.setStyle("-fx-font-size: 125%");
        }
        hbox.getChildren().add(label);
        this.setExpanded(true);
    }

    public CustomTreeItem (HBox hbox, String buddyDisplayName, Image buddyIconImage) {
        super(hbox);
        this.isUser = true;
        this.label = new Label(buddyDisplayName);
        this.buddyDisplayName = buddyDisplayName;
        this.buddyIcon = new ImageView(buddyIconImage);
        hbox.setPadding(new Insets(0, 10, 0, 0));
        hbox.getChildren().addAll(buddyIcon, label);
    }

    public boolean isRecentlyLoggedOff() {
        return recentlyLoggedOff;
    }

    public void setRecentlyLoggedOff(boolean recentlyLoggedOff) {
        label.setText("(" + this.buddyDisplayName + ")");
        this.recentlyLoggedOff = recentlyLoggedOff;
    }

    public boolean isRecentlyLoggedOn() {
        return recentlyLoggedOn;
    }

    public void setRecentlyLoggedOn(boolean recentlyLoggedOn) {
        label.setText(this.buddyDisplayName + "*");
        this.recentlyLoggedOn = recentlyLoggedOn;
    }

    public void resetLabel() {
        label.setText(buddyDisplayName);
    }

    public boolean isUser() {
        return this.isUser;
    }

    public String getBuddyDisplayName() {
        return this.buddyDisplayName;
    }
}
