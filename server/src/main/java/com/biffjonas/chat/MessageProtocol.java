package com.biffjonas.chat;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

public class MessageProtocol {
    public MessageProtocol() {
    }

    public Map<String, String> parseMessage(String message) {
        String[] messageParts = message.split("\n\n");
        String messageHead = messageParts[0];
        String messageBody = messageParts[1];
        Map<String, String> parsedMessage = new HashMap<>();

        String[] messageHeaders = messageHead.split("\n");
        for (String line : messageHeaders) {
            String[] header = line.split(": ");

            String headerKey = header[0];
            String headerValue = header[1];

            parsedMessage.put(headerKey, headerValue);
        }
        parsedMessage.put("body", messageBody);
        return parsedMessage;
    }

    public String addMessageProtocol(String message) {
        StringBuilder messageBuilder = new StringBuilder();

        Date date = new java.util.Date();
        String timestamp = date.toString();

        messageBuilder.append("Content-Length: " + message.getBytes().length + "\n");
        messageBuilder.append("TimeStamp: " + timestamp + "\n");
        messageBuilder.append("\n");
        messageBuilder.append(message);
        messageBuilder.append("\r");

        return messageBuilder.toString();
    }

    public String getMPBody(String message) {
        return message.split("\n\n")[1];
    }

}
