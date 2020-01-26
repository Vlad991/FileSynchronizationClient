package com.filesynch.client;

import com.filesynch.dto.ClientInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInt extends Remote {
    public boolean sendLogin(String login) throws RemoteException;
    public ClientInfo getClientInfo() throws RemoteException;
    public void sendTextMessage(String message) throws RemoteException;
    public boolean sendFileInfo(FileInfo fileInfo) throws RemoteException;
    public boolean sendFilePart(FilePart filePart) throws RemoteException;

    public boolean sendFileInParts(String filename, byte[] data, int length) throws RemoteException;
}
