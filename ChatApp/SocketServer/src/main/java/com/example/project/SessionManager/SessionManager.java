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
        Queue<Message> queue = outgoingQueue.get(recipient);
        if (queue == null || queue.size() == 0) {
            return new Message(true);
        } else {
            return queue.remove();
        }
    }

    public void addOutgoingMessage(String recipient, Message message) {
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

    public boolean isOnline(String username) {
        if (clients.containsKey(username)) return true;
        return false;
    }

    public static void addMember(String username, Socket clientSocket) {
        clients.put(username, clientSocket);
    }

    public static void removeMember(String username) {
        clients.remove(username);
    }

    public static void printClientTracker() {
        System.out.println( clients.keySet());
    }

    public static Socket getClientConnection(String username) {
        return clients.get(username);
    }

}
