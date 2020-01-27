package com.filesynch.dto;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

@Entity
@Table(name = "client_list")
public class ClientInfo implements Serializable {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private Long id;
    @Column(name = "name")
    private String login; // name to be logged in to server (login)
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "pc_name")
    private String pcName;
    @Column(name = "pc_model")
    private String pcModel;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClientStatus status;


    public ClientInfo() {
        getIpAddress();
        try {
            getPcName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        getPcModel();
    }

    public String getIpAddress() {
        try {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));
            ipAddress = sc.readLine().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPcName() throws UnknownHostException {
        pcName = getInetAddress().getHostName();
        return pcName;
    }

    public String getPcModel() {
        String nameOS = System.getProperty("os.name");
        String osType = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        pcModel = "OS Name: " + nameOS + ", OS Type: " + osType + ", OS Version: " + osVersion;
        return pcModel;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    private InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "login='" + login + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", pcName='" + pcName + '\'' +
                ", pcModel='" + pcModel + '\'' +
                ", status=" + status +
                '}';
    }
}
