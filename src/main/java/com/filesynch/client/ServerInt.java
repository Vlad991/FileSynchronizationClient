package com.filesynch.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInt extends Remote {
    public String login(ClientInt clientInt) throws RemoteException;
    public String sendAndReceiveTextMessage(String message) throws RemoteException;

}
