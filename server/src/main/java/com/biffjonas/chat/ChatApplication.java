package com.biffjonas.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatApplication {
    private static final int PORT = 8080;
    private static final List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            // MessageProtocol mp = new MessageProtocol();
            // String message = mp.addMessageProtocol("hello");
            // Map<String, String> parsedMessage = mp.parseMessage(message);
            // String messageBody = parsedMessage.getOrDefault("body", "nothing");
            // System.out.println(messageBody);

            while (true) {
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Create a new thread for each connected client
                final Thread clientThread = new Thread(new ClientHandler(clientSocket, clientWriters));
                clientThread.start();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
