package net.lyrt.rollback.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by nguonly on 6/27/17.
 */
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
        //String fMsg = AppState.channel.prepareChannelForSending(data);
        String fMsg = channel.invoke("prepareChannelForSending", String.class, data);
        output.println(fMsg);
    }

    public String receive() {
        try {
            String data = input.readLine();
            return data;
//            return AppState.channel.prepareChannelForReceiving(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
