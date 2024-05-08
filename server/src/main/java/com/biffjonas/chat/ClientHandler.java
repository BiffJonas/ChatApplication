package com.biffjonas.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket client;
    private String clientName;
    private Scanner scanner;
    private PrintWriter out;
    private MessageProtocol mp = new MessageProtocol();
    BufferedReader in;
    private final List<PrintWriter> clientWriters;

    public ClientHandler(final Socket client, final List<PrintWriter> clientWriters) {
        this.client = client;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try {
            scanner = new Scanner(client.getInputStream());
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Wait for client to enter a name
            String nameRequest = mp.addMessageProtocol("Enter your name: ");
            System.out.println(nameRequest);
            out.println(nameRequest);
            if (scanner.hasNextLine()) {
                clientName = scanner.nextLine();
                System.out.println("Client name received: " + clientName);
                String welcomeMessage = mp.addMessageProtocol("Welcome " + clientName + "!");
                out.println(welcomeMessage);

            }

            // Add client's PrintWriter to the list
            synchronized (ChatApplication.class) {
                clientWriters.add(out);
            }

            // Contiously read messages from clients
            final Thread readClientMessagesThread = new Thread(readMessageTask());
            readClientMessagesThread.start();

            while (!client.isClosed()) {
                // The finally blocks gets run if there isn't something stopping it.
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            // Remove client's PrintWriter from the list when client disconnects
            System.out.println("Removing client: " + clientName);
            synchronized (ChatApplication.class) {
                clientWriters.remove(out);
            }
        }
    }

    public synchronized void broadcast(final String message) {
        for (final PrintWriter writer : clientWriters) {
            if (!writer.equals(out)) {
                writer.println(clientName + ": " + message);
                writer.flush();
            }
        }
    }

    private Runnable readMessageTask() {
        return () -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    readMessageLines();
                }
                System.out.println("Client disconnected");
                out.close();
                client.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        };
    }

    private void readMessageLines() throws IOException {
        StringBuilder messageBuilder = new StringBuilder();
        int character;
        while ((character = in.read()) != -1) {
            char c = (char) character;
            messageBuilder.append(c);
            if (c == '\r') {
                String message = messageBuilder.toString();
                String messageBody = mp.getMPBody(message);
                System.out.println(messageBody);
                messageBuilder.setLength(0);
                return;
            }
        }
    }
}
