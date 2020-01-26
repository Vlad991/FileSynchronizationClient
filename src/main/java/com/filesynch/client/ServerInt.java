package com.filesynch.client;

import com.filesynch.dto.ClientInfo;
import com.filesynch.dto.FileInfo;
import com.filesynch.dto.FilePart;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInt extends Remote {
    public String login(ClientInt clientInt) throws RemoteException; // try to connect and receive login-name
    public String sendAndReceiveTextMessage(String message) throws RemoteException;
    public String sendCommand(String command) throws RemoteException; // TODO send command but receive smth
    public boolean sendFileInfo(FileInfo fileInfo) throws RemoteException;
    public boolean sendFilePart(FilePart filePart) throws RemoteException; // cycle for sending all file parts is on Client (class)
}
