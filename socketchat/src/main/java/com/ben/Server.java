package com.ben;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    public ArrayList<UserHandler> users;
    public String name;
    public String localAddress;
    public Integer port;
    public ServerSocket socket;

    public Server(String name, String localAddress, Integer port) {
        this.users = new ArrayList<>();
        this.name = name;
        this.localAddress = localAddress;
        this.port = port;
    }
    public synchronized void broadcast(String message, UserHandler sender) {
        for (UserHandler user : users) {
            if (user != sender) {
                user.sendMessage(message);
            }
        }
    }

    public synchronized void addUser(UserHandler user) {
        users.add(user);
    }

    public synchronized void removeUser(UserHandler user) {
        users.remove(user);
        broadcast(user.name + " has left the chat.", user);
    }

    @Override
    public void run() {
        try {
            this.socket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) { 
            try {
                Socket clientSocket = this.socket.accept();
                UserHandler user = new UserHandler(clientSocket);
                user.server = this;
                addUser(user);
                System.out.println("New user connected.");
                user.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}