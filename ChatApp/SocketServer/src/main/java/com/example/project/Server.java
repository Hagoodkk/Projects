package com.example.project;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int portNumber = 10007;
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

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected.");
                ChatThread chatThread = new ChatThread(clientSocket);
                Thread newThread = new Thread(chatThread);
                newThread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
