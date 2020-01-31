package com.filesynch.gui;

import com.filesynch.Main;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileSynchronizationClient {
    @Getter
    private JPanel jPanelClient;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private JPanel jPanelMain;
    private JPanel jPanelTextMessage;
    private JPanel jPanelLog;
    private JPanel jPanelFile;
    private JPanel jPanelCommand;
    private JTextField jTextFieldTextMessage;
    private JButton jButtonTextMessage;
    private JLabel jLabelTextMessage;
    private JTextField jTextFieldFile;
    private JButton jButtonSendFile;
    private JTextField jTextFieldCommand;
    private JButton jButtonSendCommand;
    @Getter
    private JTextArea jTextAreaLog;
    private JLabel jLabelFileTitle;
    private JLabel jLabelFile;
    private JLabel jLabelCommand;
    @Getter
    private JProgressBar jProgressBarFile;
    private JButton sendAllFilesButton;
    private JButton updateDBButton;

    public FileSynchronizationClient() {
        jButtonTextMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = jTextFieldTextMessage.getText();
                Main.sendMessage(message);
            }
        });
        jButtonSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file = jTextFieldFile.getText();
                Runnable task = () -> {
                    Main.sendFile(file);
                };
                new Thread(task).start();
            }
        });
        sendAllFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    Main.sendAllFiles();
                }).start();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("File Synchronization Client");
        frame.setContentPane(new FileSynchronizationClient().jPanelClient);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
