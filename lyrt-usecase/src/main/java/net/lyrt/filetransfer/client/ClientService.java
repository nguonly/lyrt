package net.lyrt.filetransfer.client;

import net.lyrt.filetransfer.server.Command;
import net.lyrt.filetransfer.server.Communicator;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientService extends Thread{
    private JTextArea txtMsg;
    private ClientCommunicator communicator;
    Socket client;

    final List<String> locker = new LinkedList<>();

    ClientService(JTextArea txtMsg){
        this.txtMsg = txtMsg;
    }

    public void run(){
        try {
            client = new Socket("localhost", 8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
        communicator = new ClientCommunicator(client);

        while(true) {
            synchronized (locker) {
                while (locker.isEmpty()) {
                    try {
                        locker.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String msg = locker.remove(0);

                if(msg.contains(Command.QUIT)) {
                    sendQuit();
                    break;
                }
                if(msg.contains(Command.GET)) sendRequest(msg);
            }
        }
    }

    public void sendCommand(String msg){
        synchronized (locker){
            locker.add(msg);
            locker.notify();
        }
    }

    public void sendRequest(String command){
        communicator.send(command);
        String[] m = command.split(" ");
        txtMsg.append("Requesting a file with " + m[1] + " chunks\n");
        receiveFileChunks(m[1]);
        txtMsg.append("File chunks are downloaded.\n");
    }

    public void receiveFileChunks(String numChunk){
        int n = Integer.parseInt(numChunk);
        for(int i=0; i<n; i++){
            String msg = communicator.receive();
            txtMsg.append(msg + "\n");
        }
    }

    public void sendQuit() {
        txtMsg.append("Client sends :::: QUIT\n");
        communicator.send(Command.QUIT);
    }

    public String getClientName(){
        return client.getLocalAddress() + ":" + client.getLocalPort();
    }
}
