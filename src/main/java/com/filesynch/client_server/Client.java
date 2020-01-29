package com.filesynch.client_server;

import com.filesynch.configuration.DataConfig;
import com.filesynch.converter.ClientInfoConverter;
import com.filesynch.converter.FileInfoConverter;
import com.filesynch.converter.FilePartConverter;
import com.filesynch.converter.TextMessageConverter;
import com.filesynch.dto.*;
import com.filesynch.entity.ClientInfo;
import com.filesynch.entity.FileInfo;
import com.filesynch.entity.FilePart;
import com.filesynch.entity.TextMessage;
import com.filesynch.repository.ClientInfoRepository;
import com.filesynch.repository.FileInfoRepository;
import com.filesynch.repository.FilePartRepository;
import com.filesynch.repository.TextMessageRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

@Component
public class Client extends UnicastRemoteObject implements ClientInt {
    private ClientInfoDTO clientInfoDTO;
    private ClientInfo clientInfo;
    private ServerInt server;
    private ClientInfoConverter clientInfoConverter;
    private FileInfoConverter fileInfoConverter;
    private FilePartConverter filePartConverter;
    private TextMessageConverter textMessageConverter;
    private ClientInfoRepository clientInfoRepository;
    private FileInfoRepository fileInfoRepository;
    private FilePartRepository filePartRepository;
    private TextMessageRepository textMessageRepository;
    private final int FILE_PART_SIZE = 1; // in bytes (1 B)
    private final String FILE_INPUT_DIRECTORY = "src/main/resources/in/";
    private final String FILE_OUTPUT_DIRECTORY = "src/main/resources/out/";

    public Client(ServerInt serverInt) throws RemoteException {
        super();
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(DataConfig.class);
        ctx.refresh();
        clientInfoRepository = ctx.getBean(ClientInfoRepository.class);
        fileInfoRepository = ctx.getBean(FileInfoRepository.class);
        filePartRepository = ctx.getBean(FilePartRepository.class);
        textMessageRepository = ctx.getBean(TextMessageRepository.class);
        clientInfoConverter = new ClientInfoConverter();
        fileInfoConverter = new FileInfoConverter(clientInfoConverter);
        filePartConverter = new FilePartConverter(clientInfoConverter, fileInfoConverter);
        textMessageConverter = new TextMessageConverter(clientInfoConverter);

        Optional<ClientInfo> clientInfoOptional = clientInfoRepository.findById(1L);
        if (!clientInfoOptional.isPresent()) {
            clientInfo = new ClientInfo();
            clientInfo.setStatus(ClientStatus.CLIENT_STANDBY);
            clientInfoDTO = clientInfoConverter.convertToDto(clientInfo);
        } else {
            clientInfo = clientInfoOptional.get();
            clientInfo.setStatus(ClientStatus.CLIENT_STANDBY);
            clientInfoDTO = clientInfoConverter.convertToDto(clientInfo);
        }
        this.server = serverInt;
    }

    public boolean sendLoginToClient(String login) {
        clientInfoDTO.setLogin(login);
        clientInfo = clientInfoConverter.convertToEntity(clientInfoDTO);
        clientInfoRepository.save(clientInfo);
        clientInfo = clientInfoRepository.findByLogin(clientInfo.getLogin());
        return true;
    }

    public ClientInfoDTO getClientInfoFromClient() {
        return clientInfoDTO;
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

    public boolean sendFileInfoToClient(FileInfoDTO fileInfoDTO) {
        FileInfo fileInfo = fileInfoConverter.convertToEntity(fileInfoDTO);
        fileInfo.setClient(clientInfo);
        fileInfoRepository.save(fileInfo);
        System.out.println(fileInfo);
        return true;
    }

    //todo (not correct)?
    public boolean sendFilePartToClient(FilePartDTO filePartDTO) {
        try {
            File file = new File(FILE_INPUT_DIRECTORY + filePartDTO.getFileInfoDTO().getName());
            if (filePartDTO.isFirst()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file, true);
            out.write(filePartDTO.getData(), 0, filePartDTO.getLength());
            out.flush();
            out.close();
            FilePart filePart = filePartConverter.convertToEntity(filePartDTO);
            filePart.setClient(clientInfo);
            FileInfo fileInfo = fileInfoRepository.findByNameAndSizeAndClient(
                    filePart.getFileInfo().getName(),
                    filePart.getFileInfo().getSize(),
                    clientInfo);
            if (fileInfo != null) {
                filePart.setFileInfo(fileInfo);
            }
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
        setClientStatus(ClientStatus.CLIENT_FIRST);
        if (!isLoggedIn()) {
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
            clientInfoDTO.setLogin(login);
            clientInfoDTO.setStatus(ClientStatus.CLIENT_SECOND);
            clientInfoRepository.save(clientInfo);
            System.out.println("Log in success!");
        } else {
            try {
                server.loginToServer(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    // calls here
    public String sendTextMessageToServer(String message) {
        setClientStatus(ClientStatus.CLIENT_WORK);
        if (isLoggedIn()) {
            String answer = null;
            try {
                answer = server.sendAndReceiveTextMessageFromServer(clientInfo.getLogin(), message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // todo save TextMessage to db
            System.out.println(answer);
            return answer;
        } else {
            return "You are not logged in!";
        }
    }

    // this is cycle for sending file parts from client to server, it calls here
    public boolean sendFileToServer(String filename) {
        if (isLoggedIn()) {
            setClientStatus(ClientStatus.CLIENT_WORK);
            try {
                String filePathname = FILE_OUTPUT_DIRECTORY + filename;
                File file = new File(filePathname);
                FileInputStream in = new FileInputStream(file);

                FileInfoDTO fileInfoDTO = new FileInfoDTO();
                fileInfoDTO.setName(filename);
                fileInfoDTO.setSize(file.length());
                fileInfoDTO.setClient(clientInfoDTO);
                server.sendFileInfoToServer(clientInfoDTO.getLogin(), fileInfoDTO);

                byte[] fileData = new byte[FILE_PART_SIZE];
                int fileLength = in.read(fileData);
                boolean step = true;
                while (fileLength > 0) {
                    System.out.println(fileLength);
                    FilePartDTO filePartDTO = new FilePartDTO();
                    if (step) {
                        filePartDTO.setFirst(true);
                    } else {
                        filePartDTO.setFirst(false);
                        step = false;
                    }
                    filePartDTO.setHashKey((long) fileData.hashCode());
                    filePartDTO.setFileInfoDTO(fileInfoDTO);
                    filePartDTO.setData(fileData);
                    filePartDTO.setLength(fileLength);
                    filePartDTO.setStatus(FilePartStatus.NOT_SENT);
                    filePartDTO.setClient(clientInfoDTO);
                    System.out.println(server.sendFilePartToServer(clientInfoDTO.getLogin(), filePartDTO));
                    // todo check for "true" from method sendFilePart()!!!!!!!!!!!!
                    fileLength = in.read(fileData);
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("You are not logged in!");
            return false;
        }

        return true;
    }

    private void setClientStatus(ClientStatus clientStatus) {
        clientInfo.setStatus(clientStatus);
        clientInfoDTO.setStatus(clientStatus);
    }

    private boolean isLoggedIn() {
        return clientInfo.getLogin() != null;
    }
}
