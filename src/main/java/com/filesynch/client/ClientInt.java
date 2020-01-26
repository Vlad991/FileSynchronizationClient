package com.filesynch.client;

import com.filesynch.dto.ClientInfo;
import com.filesynch.dto.FileInfo;
import com.filesynch.dto.FilePart;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInt extends Remote {
    public boolean sendLogin(String login) throws RemoteException;
    public ClientInfo getClientInfo() throws RemoteException;
    public void sendTextMessage(String message) throws RemoteException;
    public boolean sendFileInfo(FileInfo fileInfo) throws RemoteException;
    public boolean sendFilePart(FilePart filePart) throws RemoteException; // cycle for sending all file parts is on Server (class)
}
