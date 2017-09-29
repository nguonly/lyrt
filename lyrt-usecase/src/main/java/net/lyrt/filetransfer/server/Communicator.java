package net.lyrt.filetransfer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Communicator {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Channel channel;

    public Communicator(Socket socket, Channel channel){
        this.socket = socket;
        this.channel = channel;
        try {
            input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            output = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void send(String data) {
        String msg = channel.invoke("format", String.class, data);
        output.println(msg);
    }

    public String receive() {
        try {
            String msg = input.readLine();
            return channel.invoke("unformat", String.class, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
