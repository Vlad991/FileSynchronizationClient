package com.filesynch;

import com.filesynch.client_server.Client;
import com.filesynch.client_server.ServerInt;

import java.rmi.Naming;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServerInt server = (ServerInt) Naming.lookup("rmi://localhost/fs");
            Client c = new Client(server);
            c.loginToServer();
            c.sendTextMessageToServer("Hello!!!");
            //sendMessages(c);
            sendFiles(c);
            //c.sendFileToServer("video.mp4");
            System.out.println("End.....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessages(Client c) {
        Scanner s = new Scanner(System.in);
        boolean run = true;
        while (run) {
            String line = s.nextLine();
            if (line.equals("stop")) {
                run = false;
                break;
            }
            c.sendTextMessageToServer(line);
        }
    }

    public static void sendFiles(Client c) {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        while (run) {
            String line = scanner.nextLine();
            if (line.equals("stop")) {
                run = false;
                break;
            }
            c.sendFileToServer(line);
        }
    }
}
