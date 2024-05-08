package com.biffjonas.chat;

import com.biffjonas.chat.MessageProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8080;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Scanner scanner;
    private static MessageProtocol mp = new MessageProtocol();
    private static Socket socket;

    public static void main(String[] args) {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server. You can start typing messages.");
            // count();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);

            // Start a thread to continuously read messages from the server
            Thread readThread = new Thread(readMessageTask());
            readThread.start();

            // Main thread for sending messages to the server
            Thread sendThread = new Thread(sendMessageTask());
            sendThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void count() {
        int max = 10;
        for (int i = 0; i < max; i++) {
            System.out.println("Index: " + i);

        }
    }

    private static Runnable readMessageTask() {
        return () -> {
            try {
                int character;
                StringBuilder messageBuilder = new StringBuilder();
                while ((character = in.read()) != -1) {
                    char c = (char) character;
                    messageBuilder.append(c);

                    if (c == '\r') {
                        String message = messageBuilder.toString();
                        String messageBody = mp.getMPBody(message);
                        System.out.println(messageBody);
                        messageBuilder.setLength(0);
                    }
                }
                System.out.println("Server out of service");
                out.close();
                socket.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
    }

    private static Runnable sendMessageTask() {
        return () -> {
            String message;
            while (true) {
                message = scanner.nextLine();
                String protocolMessage = mp.addMessageProtocol(message);
                out.println(protocolMessage);
                out.flush();
            }
        };
    }
}
