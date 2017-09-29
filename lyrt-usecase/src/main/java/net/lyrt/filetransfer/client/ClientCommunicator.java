package net.lyrt.filetransfer.client;

import net.lyrt.filetransfer.server.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientCommunicator {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public ClientCommunicator(Socket socket){
        this.socket = socket;

        try {
            input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            output = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void send(String data) {
        output.println(data);
    }

    public String receive() {
        try {
            return input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
