package com.verde.advent.y2016;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static class DeadEnd {
        static void read(Socket s) {
            Runnable r = () -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                    for (String line=null; (line = in.readLine()) != null; ) {
                        System.out.printf("[%s] %s\n", Thread.currentThread().getName(), line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        
            System.out.printf("[%s] Processing client connection from %s %s\n", Thread.currentThread().getName(), s.getInetAddress().getHostName(), s.getInetAddress().getHostAddress());
            new Thread(r).start();
        }
    }
    
    public static void main(String[] args) throws Exception {
        int port = args.length > 1 ? Integer.parseInt(args[0]) : 80;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("[%s] Listening on port %d\n", Thread.currentThread().getName(), port);

            while (true) {
                DeadEnd.read(serverSocket.accept());
            }
        }
    }
}
