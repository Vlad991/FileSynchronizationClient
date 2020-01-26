package com.filesynch.client;

import com.filesynch.dto.ClientInfo;
import com.filesynch.dto.FileInfo;
import com.filesynch.dto.FilePart;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInt {
    private ClientInfo clientInfo;
    private ServerInt server;

    public Client(ServerInt serverInt) throws RemoteException {
        super();
        this.clientInfo = new ClientInfo();
        this.server = serverInt;
    }

    public boolean sendLogin(String login) {
        clientInfo.setLogin(login);
        return true;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void sendTextMessage(String message) {
        System.out.println(message);
    }

    public boolean sendFileInfo(FileInfo fileInfo) {
        fileInfoDAO.save(fileInfo);
        // todo gui message
        return true;
    }

    public boolean sendFilePart(FilePart filePart) {
        //todo
    }

    // this is cycle for sending file parts from client to server, it calls here
    public boolean sendFileInParts() {
        server.sendFilePart(); // todo cycle
        try {
            filename = "src/main/resources/in/" + filename;
            File f = new File(filename);
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f, true);
            out.write(data, 0, len);
            out.flush();
            out.close();
            System.out.println("Done writing data...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
