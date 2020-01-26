package com.filesynch;

import com.filesynch.client.Client;
import com.filesynch.client.ServerInt;

import java.rmi.Naming;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Client c = new Client();
            ServerInt server = (ServerInt) Naming.lookup("rmi://localhost/fs");
            String login = server.login(c);
            System.out.println("Listening.....");
            Scanner s = new Scanner(System.in);
            while (true) {
                String line = s.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
