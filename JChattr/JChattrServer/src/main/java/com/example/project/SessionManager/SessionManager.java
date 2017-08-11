package com.example.project.SessionManager;

import com.example.project.Serializable.Message;

import java.net.Socket;
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

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
            clients = new HashMap<>();
            outgoingQueue = new HashMap<>();
        }
        return sessionManager;
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
