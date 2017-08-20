package com.example.project;
import com.example.project.DatabaseManager.DatabaseManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int portNumber = 10007;

    public static void main(String[] args) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.createTables();

        ServerSocket serverSocket = startServer(portNumber);

        if (serverSocket == null) System.exit(1);

        listenForClientConnections(serverSocket);
    }

    public static ServerSocket startServer(int portNumber) {
        try {
            return new ServerSocket(portNumber);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void listenForClientConnections(ServerSocket serverSocket) {
        Socket clientSocket;
        System.out.println("Listening on port " + portNumber);

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected from address " + clientSocket.getInetAddress());
                ChatThread chatThread = new ChatThread(clientSocket);
                Thread newThread = new Thread(chatThread);
                newThread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
