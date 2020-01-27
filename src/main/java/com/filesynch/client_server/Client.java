package com.filesynch.client_server;

import com.filesynch.dto.*;
import com.filesynch.repository.FileInfoRepository;
import com.filesynch.repository.FilePartRepository;
import com.filesynch.repository.TextMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@Component
public class Client extends UnicastRemoteObject implements ClientInt {
    private ClientInfo clientInfo;
    private ServerInt server;
    @Autowired
    private FileInfoRepository fileInfoRepository;
    @Autowired
    private FilePartRepository filePartRepository;
    @Autowired
    private TextMessageRepository textMessageRepository;
    private final int FILE_MULTIPLICITY = 10;
    private final String FILE_INPUT_DIRECTORY = "src/main/resources/in/";
    private final String FILE_OUTPUT_DIRECTORY = "src/main/resources/out/";

    public Client(ServerInt serverInt) throws RemoteException {
        super();
        this.clientInfo = new ClientInfo();
        this.server = serverInt;
        clientInfo.setStatus(ClientStatus.CLIENT_STANDBY);
    }

    public boolean sendLoginToClient(String login) {
        clientInfo.setLogin(login);
        return true;
    }

    public ClientInfo getClientInfoFromClient() {
        return clientInfo;
    }

    public void sendTextMessageToClient(String message) {
        TextMessage textMessage = new TextMessage();
        textMessage.setMessage(message);
        textMessageRepository.save(textMessage);
        System.out.println(message);
    }

    public boolean sendCommandToClient(String command) {
        System.out.println(command);
        return true;
    }

    public boolean sendFileInfoToClient(FileInfo fileInfo) {
        fileInfoRepository.save(fileInfo);
        System.out.println(fileInfo);
        return true;
    }

    //todo (not correct)?
    public boolean sendFilePartToClient(FilePart filePart) {
        try {
            File file = new File(filePart.getFileInfo().getName() + FILE_INPUT_DIRECTORY);
            if (filePart.isFirst()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file, true);
            out.write(filePart.getData(), 0, filePart.getLength());
            out.flush();
            out.close();
            filePartRepository.save(filePart);
            System.out.println(filePart);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // calls here
    public boolean loginToServer() {
        clientInfo.setStatus(ClientStatus.CLIENT_FIRST);
        String login = null;
        try {
            login = server.loginToServer(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (login == null) {
            System.out.println("Log in failed!");
            return false;
        }
        clientInfo.setLogin(login);
        clientInfo.setStatus(ClientStatus.CLIENT_SECOND);
        System.out.println("Log in success!");
        return true;
    }

    // calls here
    public String sendTextMessageToServer(String message) {
        clientInfo.setStatus(ClientStatus.CLIENT_WORK);
        String answer = null;
        try {
            answer = server.sendAndReceiveTextMessageFromServer(clientInfo.getLogin(), message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // todo save TextMessage to db
        System.out.println(answer);
        return answer;
    }

    // this is cycle for sending file parts from client to server, it calls here
    public boolean sendFileToServer(String filename) throws RemoteException {
        clientInfo.setStatus(ClientStatus.CLIENT_WORK);
        try {
            String filePathname = FILE_OUTPUT_DIRECTORY + filename;
            File file = new File(filePathname);
            FileInputStream in = new FileInputStream(file);

            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(filename);
            fileInfo.setSize(file.length());
            server.sendFileInfoToServer(clientInfo.getLogin(), fileInfo);

            byte[] fileData = new byte[1024 * 1024];
            int fileLength = in.read(fileData);
            boolean step = true;
            while (fileLength > 0) {
                System.out.println(fileLength);
                FilePart filePart = new FilePart();
                if (step) {
                    filePart.setFirst(true);
                } else {
                    filePart.setFirst(false);
                    step = false;
                }
                filePart.setHashKey((long) fileData.hashCode());
                filePart.setFileInfo(fileInfo);
                filePart.setData(fileData);
                filePart.setLength(fileLength);
                filePart.setStatus(FilePartStatus.NOT_SENT);
                System.out.println(server.sendFilePartToServer(clientInfo.getLogin(), filePart));
                // todo check for "true" from method sendFilePart()!!!!!!!!!!!!
                fileLength = in.read(fileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
