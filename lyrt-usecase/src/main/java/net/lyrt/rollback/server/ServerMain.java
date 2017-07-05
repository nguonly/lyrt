package net.lyrt.rollback.server;

import net.lyrt.Registry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by nguonly on 6/27/17.
 */
public class ServerMain {

    public static void main(String... args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(33333);

        while(true){
            System.out.println("Waiting for client...");

            Socket client = serverSocket.accept();
            System.out.println(String.format("Got connection from %s:%d ", client.getInetAddress().toString(), client.getPort()));

            ServiceHandler handler = new ServiceHandler(client);
            handler.start();
        }
    }
}
