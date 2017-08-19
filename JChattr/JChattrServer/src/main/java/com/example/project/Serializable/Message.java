package com.example.project.Serializable;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable{
    private String sender, recipient, message, salt, passwordSaltedHash, logOn, logOut, chatroomMessage;
    private boolean error, nullMessage, buddyListUpdate, loginRequest, saltRequest, logOnEvent, logOutEvent, chatroomListingsRequest, chatroomCreateRequest, leftChatroom, enteredChatroom, carryingChatroomMessage;
    private BuddyList buddyList;
    private String senderDisplayName;
    private HashMap<String, ArrayList<String>> categories, users;
    private Pair<String, String> chatroomCategoryAndName;
    private ArrayList<String> chatroomUsers;

    public String getChatroomMessage() {
        return chatroomMessage;
    }

    public void setChatroomMessage(String chatroomMessage) {
        this.chatroomMessage = chatroomMessage;
    }

    public boolean isCarryingChatroomMessage() {
        return carryingChatroomMessage;
    }

    public void setCarryingChatroomMessage(boolean carryingChatroomMessage) {
        this.carryingChatroomMessage = carryingChatroomMessage;
    }

    public ArrayList<String> getChatroomUsers() {
        return chatroomUsers;
    }

    public void setChatroomUsers(ArrayList<String> chatroomUsers) {
        this.chatroomUsers = chatroomUsers;
    }

    public boolean isLeftChatroom() {
        return leftChatroom;
    }

    public void setLeftChatroom(boolean leftChatroom) {
        this.leftChatroom = leftChatroom;
    }

    public boolean isEnteredChatroom() {
        return enteredChatroom;
    }

    public void setEnteredChatroom(boolean enteredChatroom) {
        this.enteredChatroom = enteredChatroom;
    }

    public Pair<String, String> getChatroomCategoryAndName() {
        return chatroomCategoryAndName;
    }

    public void setChatroomCategoryAndName(Pair<String, String> chatroomCategoryAndName) {
        this.chatroomCategoryAndName = chatroomCategoryAndName;
    }

    public boolean isChatroomCreateRequest() {
        return chatroomCreateRequest;
    }

    public void setChatroomCreateRequest(boolean chatroomCreateRequest) {
        this.chatroomCreateRequest = chatroomCreateRequest;
    }

    public HashMap<String, ArrayList<String>> getCategories() {
        return this.categories;
    }

    public void setCategories(HashMap<String, ArrayList<String>> categories) {
        this.categories = categories;
    }

    public HashMap<String, ArrayList<String>> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, ArrayList<String>> users) {
        this.users = users;
    }

    public boolean isChatroomListingsRequest() {
        return chatroomListingsRequest;
    }

    public void setChatroomListingsRequest(boolean chatroomListingsRequest) {
        this.chatroomListingsRequest = chatroomListingsRequest;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getLogOn() {
        return logOn;
    }

    public void setLogOn(String logOn) {
        this.logOn = logOn;
    }

    public String getLogOut() {
        return logOut;
    }

    public void setLogOut(String logOut) {
        this.logOut = logOut;
    }

    public boolean isLogOnEvent() {
        return logOnEvent;
    }

    public void setLogOnEvent(boolean logOnEvent) {
        this.logOnEvent = logOnEvent;
    }

    public boolean isLogOutEvent() {
        return logOutEvent;
    }

    public void setLogOutEvent(boolean logOutEvent) {
        this.logOutEvent = logOutEvent;
    }

    public String getPasswordSaltedHash() {
        return passwordSaltedHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isSaltRequest() {
        return saltRequest;
    }

    public void setSaltRequest(boolean saltRequest) {
        this.saltRequest = saltRequest;
    }

    public void setPasswordSaltedHash(String passwordSaltedHash) {
        this.passwordSaltedHash = passwordSaltedHash;
    }

    public boolean isLoginRequest() {
        return loginRequest;
    }

    public void setLoginRequest(boolean loginRequest) {
        this.loginRequest = loginRequest;
    }

    public Message() {}

    public Message(boolean nullMessage) {
        if (nullMessage) this.nullMessage = true;
    }

    public Message(String sender, String displayName, String recipient, String message) {
        this.sender = sender;
        this.senderDisplayName = displayName;
        this.recipient = recipient;
        this.message = message;
        this.error = false;
        this.nullMessage = false;
        this.buddyListUpdate = false;
    }

    public boolean isBuddyListUpdate() {
        return buddyListUpdate;
    }

    public void setBuddyListUpdate(boolean buddyListUpdate) {
        this.buddyListUpdate = buddyListUpdate;
    }

    public BuddyList getBuddyList() {
        return buddyList;
    }

    public void setBuddyList(BuddyList buddyList) {
        this.buddyList = buddyList;
    }

    public String getMessage() {
        return message;
    }

    public boolean isNullMessage() {
        return nullMessage;
    }

    public void setNullMessage(boolean nullMessage) {
        this.nullMessage = nullMessage;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
