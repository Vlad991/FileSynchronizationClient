package com.filesynch.client;

import com.filesynch.dto.ClientInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInt {
    private ClientInfo clientInfo;

    public Client() throws RemoteException {
        super();
        this.clientInfo = new ClientInfo();
    }

    public boolean sendLogin(String login) {
        clientInfo.setLogin(login);
        return true;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public boolean sendFileInParts(String filename, byte[] data, int len) throws RemoteException {
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
