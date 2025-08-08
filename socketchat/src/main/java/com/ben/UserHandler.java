package com.ben;

import java.net.Socket;


public class UserHandler extends Thread {
    public String name;
    public Server server;
    public Socket socket;

    public UserHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            boolean named = false;
            while (true) {
                int bytesRead = socket.getInputStream().read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                String message = new String(buffer, 0, bytesRead).trim();
                if (!named && message.startsWith("/name ")) {
                    name = message.substring(6);
                    named = true;
                    server.broadcast(name + " has joined the chat.", this);
                } else if (named) {
                    server.broadcast(name + ": " + message, this);
                } else {
                    sendMessage("Please set your name with /name <yourname>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.removeUser(this);
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    public void sendMessage(String message) {
        try {
            socket.getOutputStream().write((message + "\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}