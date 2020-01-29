package com.filesynch.gui;

import com.filesynch.Main;
import com.filesynch.client_server.Client;
import com.filesynch.client_server.ServerInt;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

public class ConnectToServer {
    @Getter
    private JPanel jPanelMain;
    private JTextField jTextFieldIP;
    private JTextField jTextFieldPort;
    private JTextField jTextFieldAddress;
    private JButton jButtonConnect;
    private JLabel jLabelIP;
    private JLabel jLabelPort;
    private JLabel jLabelAddress;

    public ConnectToServer() {
        jButtonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = jTextFieldIP.getText();
                String port = jTextFieldPort.getText();
                String address = jTextFieldAddress.getText();
                Main.connectToServer(ip, port, address);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ConnectToServer");
        frame.setContentPane(new ConnectToServer().jPanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
