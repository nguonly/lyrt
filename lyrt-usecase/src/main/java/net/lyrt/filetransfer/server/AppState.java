package net.lyrt.filetransfer.server;

import javax.swing.*;
import java.net.Socket;

public class AppState {
    static ServerService serverService;
    static JTextArea txtMsg; //ServerMain Server Message in MessagePanel
    static boolean isTranquil;
    static Channel channel;
    public static DefaultListModel<ServiceHandler> listModel;
    public static JList<ServiceHandler> listClient;

    public static JTextArea getTextMessageUI(){
        return txtMsg;
    }

    public static void setTranquilState(boolean state){
        isTranquil = state;
    }

    public static void startServerService(){
        String tranquilMsg;
        if(isTranquil) tranquilMsg = "::: With Tranquility :::";
        else tranquilMsg = "::: WithOUT Tranquility :::";

        txtMsg.setText(tranquilMsg + "\n");
        serverService = new ServerService();
        serverService.start(); //Thread
    }

    public static void stopServerService(){
        if(serverService!=null && serverService.server!=null && !serverService.server.isClosed()){
            serverService.stopServer();
            txtMsg.append("Server STOP!\n");
        }
    }

    public static void adaptCompression(){
        txtMsg.append("Adapt to LZ compression\n");
        serverService.adapt("LZ");
    }

    public static void adaptEncryption(){
        txtMsg.append("Adapt to AES encyption\n");
        serverService.adapt("AES");
    }

    public static void adaptCompressionEncryption(){
        txtMsg.append("Adapt to LZ-AES composition\n");
        serverService.adapt("LZ-AES");
    }

    public static void adaptEncryptionCompression(){
        txtMsg.append("Adapt to AES-LZ composition\n");
        serverService.adapt("AES-LZ");
    }

    public static void resetAdaptation(){
        txtMsg.append("Reset adaptation\n");
        serverService.resetAdaptation();
    }

    public static void appendMessage(String message){
        txtMsg.append(message + "\n");
    }
}
