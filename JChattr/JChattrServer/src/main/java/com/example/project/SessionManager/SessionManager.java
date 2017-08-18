package com.example.project.SessionManager;

import com.example.project.Serializable.Message;
import javafx.util.Pair;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Kyle on 7/4/2017.
 */

public class SessionManager {
    private static SessionManager sessionManager;

    private static HashMap<String, Socket> clients;
    private static HashMap<String, Queue<Message>> outgoingQueue;

    private static HashMap<String, ArrayList<String>> chatroomCategories, chatroomUsers;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
            clients = new HashMap<>();
            outgoingQueue = new HashMap<>();
            initializeChatrooms();
        }
        return sessionManager;
    }

    private static void initializeChatrooms() {
        chatroomCategories = new HashMap<>();
        chatroomUsers = new HashMap<>();

        chatroomCategories.put("General", new ArrayList<>());
        chatroomCategories.put("Other", new ArrayList<>());
    }

    public static void addChatroom(String displayName, Pair<String,String> chatroomCategoryAndName) {
        chatroomCategories.get(chatroomCategoryAndName.getKey()).add(chatroomCategoryAndName.getValue());
        chatroomUsers.put(chatroomCategoryAndName.getValue(), new ArrayList<>());
        chatroomUsers.get(chatroomCategoryAndName.getValue()).add(displayName);
    }

    public static void addChatroomUser(String displayName, String chatroomCategory, String chatroomName) {
        if (chatroomCategories.get(chatroomCategory).contains(chatroomName) && chatroomUsers.get(chatroomName) != null) {
            chatroomUsers.get(chatroomName).add(displayName);
        }
    }

    public static void removeChatroomUser(String displayName, Pair<String, String> chatroomCategoryAndName) {
        String chatroomCategory = chatroomCategoryAndName.getKey();
        String chatroomName = chatroomCategoryAndName.getValue();

        chatroomUsers.get(chatroomName).remove(displayName);
        if (chatroomUsers.get(chatroomName).size() == 0) {
            chatroomCategories.get(chatroomCategory).remove(chatroomName);
            chatroomUsers.remove(chatroomName);
        }
    }

    public HashMap<String, ArrayList<String>> getChatroomCategories() {
        return chatroomCategories;
    }

    public HashMap<String, ArrayList<String>> getChatroomUsers() {
        return chatroomUsers;
    }

    public Message getNextOutgoing(String recipient) {
        recipient = recipient.toLowerCase();
        Queue<Message> queue = outgoingQueue.get(recipient);
        if (queue == null || queue.size() == 0) {
            return new Message(true);
        } else {
            return queue.remove();
        }
    }

    public static void addOutgoingMessage(String recipient, Message message) {
        recipient = recipient.toLowerCase();
        Queue<Message> queue = outgoingQueue.get(recipient);
        if (queue == null) {
            queue = new LinkedList<>();
            queue.add(message);
            outgoingQueue.put(recipient, queue);
        } else {
            queue.add(message);
            outgoingQueue.replace(recipient, queue);
        }
    }

    public static void removeStateUpdatesFromUser(String username) {
        if (outgoingQueue.get(username) == null) return;
        for (Message message : outgoingQueue.get(username)) {
            if (message.isLogOnEvent() || message.isLogOutEvent()) {
                outgoingQueue.get(username).remove(message);
            }
        }
    }

    public boolean isOnline(String username) {
        username = username.toLowerCase();
        if (clients.containsKey(username)) return true;
        return false;
    }

    public static void addMember(String username, Socket clientSocket) {
        username = username.toLowerCase();
        clients.put(username, clientSocket);
    }

    public static void removeMember(String username) {
        username = username.toLowerCase();
        clients.remove(username);
    }

    public static void printClientTracker() {
        System.out.println( clients.keySet());
    }

    public static void broadcastStateUpdate(String username, int state) {
        System.out.println("Broadcasting system update with " + username + " and state number " + state);
        Message message = new Message();
        message.setNullMessage(true);
        if (state == 0) {
            message.setLogOnEvent(true);
            message.setLogOn(username);
        } else if (state == 1) {
            message.setLogOutEvent(true);
            message.setLogOut(username);
        }

        for (String name : clients.keySet()) {
            if (!name.equals(username)) addOutgoingMessage(name, message);
        }
    }

}
