package com.example.project.SessionManager;

import ConnectionController.ConnectionController;
import com.example.project.BuddyListScreen.BuddyListScreenController;
import com.example.project.ChatWindow.ChatWindowController;
import com.example.project.ChatroomScreen.ChatroomScreenController;
import com.example.project.ChatroomWelcomeScreen.ChatroomWelcomeScreenController;
import com.example.project.Serializable.BuddyList;
import com.example.project.Serializable.Message;
import com.example.project.WelcomeScreen.WelcomeScreenController;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class SessionManager {
    private static SessionManager sessionManager;

    private ConnectionController connectionController;

    private String username;
    private String displayName;
    private Socket clientSocket;
    private BuddyList buddyList;
    private Queue<Message> outgoingQueue;
    private WelcomeScreenController welcomeScreenController;
    private BuddyListScreenController buddyListScreenController;
    private HashMap<String, ChatWindowController> chatWindowControllers;
    private ChatroomScreenController chatroomScreenController;
    private ChatroomWelcomeScreenController chatroomWelcomeScreenController;

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setConnectionInformation(String hostName, int portNumber) {
        connectionController = new ConnectionController(hostName, portNumber);
    }

    public ChatroomWelcomeScreenController getChatroomWelcomeScreenController() {
        return chatroomWelcomeScreenController;
    }

    public void setChatroomWelcomeScreenController(ChatroomWelcomeScreenController chatroomWelcomeScreenController) {
        this.chatroomWelcomeScreenController = chatroomWelcomeScreenController;
    }

    public void setChatroomScreenController(ChatroomScreenController chatroomScreenController) {
        this.chatroomScreenController = chatroomScreenController;
    }

    public ChatroomScreenController getChatroomController() {
        return this.chatroomScreenController;
    }

    public void removeChatroomScreenController() {
        if (this.chatroomScreenController != null) {
            this.chatroomScreenController = null;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void nullify() {
        username = null;
        try {
            if (clientSocket != null) clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        buddyList = null;
        outgoingQueue = new LinkedList<>();
        buddyListScreenController = null;
        chatroomWelcomeScreenController = null;
        chatWindowControllers = new HashMap<>();
        chatroomScreenController = null;
    }

    public void closeAllChatWindows() {
        for (ChatWindowController chatWindowController : chatWindowControllers.values()) {
            chatWindowController.shutdown();
        }
        if (SessionManager.getInstance().getChatroomWelcomeScreenController() != null) {
            SessionManager.getInstance().getChatroomWelcomeScreenController().shutdown();
        }
    }

    public WelcomeScreenController getWelcomeScreenController() {
        return welcomeScreenController;
    }

    public void setWelcomeScreenController(WelcomeScreenController welcomeScreenController) {
        this.welcomeScreenController = welcomeScreenController;
    }

    public void setBuddyListScreenController(BuddyListScreenController buddyListScreenController) {
        this.buddyListScreenController = buddyListScreenController;
    }

    public BuddyListScreenController getBuddyListScreenController() {
        return this.buddyListScreenController;
    }

    public void addChatWindowController(String username, String recipient, ChatWindowController chatWindowController) {
        username = username.toLowerCase();
        recipient = recipient.toLowerCase();
        chatWindowControllers.put(username + " -> " + recipient, chatWindowController);
    }

    public void removeChatWindowController(String username, String recipient) {
        username = username.toLowerCase();
        recipient = recipient.toLowerCase();
        chatWindowControllers.remove(username + " -> " + recipient);
    }

    public ChatWindowController getChatWindowController(String username, String recipient) {
        username = username.toLowerCase();
        recipient = recipient.toLowerCase();
        ChatWindowController chatWindowController = chatWindowControllers.get(username + " -> " + recipient);
        if (chatWindowController == null) chatWindowController = chatWindowControllers.get(recipient + " -> " + username);
        return chatWindowController;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Queue<Message> getOutgoingQueue() {
        return outgoingQueue;
    }

    public void addOutgoingMessage(Message message) {
        outgoingQueue.add(message);
    }

    private SessionManager() {}

    public String getUsername() { return this.username; }
    public void setUsername(String username) { this.username = username; }
    public void setBuddyList(BuddyList buddyList) { this.buddyList = buddyList; }
    public BuddyList getBuddyList() { return this.buddyList; }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
            sessionManager.outgoingQueue = new LinkedList<>();
            sessionManager.chatWindowControllers = new HashMap<>();
        }
        return sessionManager;
    }
}
