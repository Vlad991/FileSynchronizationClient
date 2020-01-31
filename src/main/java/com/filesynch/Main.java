package com.filesynch;

import com.filesynch.client_server.Client;
import com.filesynch.client_server.ServerInt;
import com.filesynch.gui.ConnectToServer;
import com.filesynch.gui.FileSynchronizationClient;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static JFrame connectToServerFrame;
    public static JFrame clientFrame;
    public static FileSynchronizationClient fileSynchronizationClient;
    public static ServerInt server;
    public static Client client;

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        connectToServerFrame = new JFrame("Connect To Server");
        connectToServerFrame.setContentPane(new ConnectToServer().getJPanelMain());
        connectToServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connectToServerFrame.pack();
        connectToServerFrame.setLocationRelativeTo(null);
        connectToServerFrame.setVisible(true);
    }

    public static void connectToServer(String ip, String port, String address) {
        fileSynchronizationClient = new FileSynchronizationClient();
        try {
            server = (ServerInt) Naming.lookup("rmi://" + ip + ":" + port + "/" + address);
            client = new Client(server);
            client.setLog(fileSynchronizationClient.getJTextAreaLog());
            client.loginToServer();
            client.sendTextMessageToServer("Connected client: " + client.getClientInfo().getLogin());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        connectToServerFrame.setVisible(false);
        clientFrame = new JFrame("File Synchronization Client");
        clientFrame.setContentPane(fileSynchronizationClient.getJPanelClient());
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.pack();
        clientFrame.setLocationRelativeTo(null);
        clientFrame.setVisible(true);
        client.setFileProgressBar(fileSynchronizationClient.getJProgressBarFile());
    }

    public static void sendMessage(String message) {
        client.sendTextMessageToServer(message);
    }

    public static void sendFile(String file) {
        client.sendFileToServer(file);
    }

    public static void sendAllFiles() {
        try (Stream<Path> walk = Files.walk(Paths.get(client.FILE_OUTPUT_DIRECTORY.substring(0,client.FILE_OUTPUT_DIRECTORY.length() - 1)))) {
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
            for (String filePath : result) {
                sendFile(filePath.replace(client.FILE_OUTPUT_DIRECTORY, ""));
            }
        } catch (IOException e) {
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
