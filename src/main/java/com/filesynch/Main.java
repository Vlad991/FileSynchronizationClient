package com.filesynch;

import com.filesynch.client_server.Client;
import com.filesynch.client_server.ServerInt;
import com.filesynch.configuration.DataConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.rmi.Naming;
import java.util.Scanner;

@Configuration
@ComponentScan("com.filesynch")
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(DataConfig.class);
        ctx.refresh();
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServerInt server = (ServerInt) Naming.lookup("rmi://localhost/fs");
            Client c = new Client(server);
            c.loginToServer();
            c.sendTextMessageToServer("Hello!!!");
            c.sendFileToServer("file.txt");
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
