package com.ben;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar socketchat.jar server <address> <port> <servername> | client <address> <port> <username>");
            return;
        }

        if (args[0].equalsIgnoreCase("server")) {
            if (args.length < 4) {
                System.out.println("Usage: java -jar socketchat.jar server <address> <port> <name>");
                return;
            }
            Server server = new Server(args[3], args[1], Integer.parseInt(args[2]));
            server.start();
            System.out.println("Server started at " + args[1] + ":" + args[2]);
        } else if (args[0].equalsIgnoreCase("client")) {
            if (args.length < 4) {
                System.out.println("Usage: java -jar socketchat.jar client <address> <port> <name>");
                return;
            }
            String address = args[1];
            int port = Integer.parseInt(args[2]);
            String name = args[3];
            try (java.net.Socket socket = new java.net.Socket(address, port)) {
                socket.getOutputStream().write(("/name " + name + "\n").getBytes());

                Thread reader = new Thread(() -> {
                    try {
                        java.io.InputStream in = socket.getInputStream();
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) != -1) {
                            System.out.print(new String(buf, 0, len));
                        }
                    } catch (Exception e) {
                    }
                });
                reader.setDaemon(true);
                reader.start();

                java.util.Scanner scanner = new java.util.Scanner(System.in);
                java.io.OutputStream out = socket.getOutputStream();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    out.write((line + "\n").getBytes());
                }
            } catch (Exception e) {
                System.out.println("Connection error: " + e.getMessage());
            }
        } else {
            System.out.println("Unknown mode: " + args[0]);
        }
    }
}