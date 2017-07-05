package net.lyrt.rollback.client;

import net.lyrt.rollback.server.Command;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by nguonly on 6/27/17.
 */
public class ClientServiceHandler extends Thread {
    private Socket socket;
    final int SERVER_PORT = 33333;
    final String SERVER_IP = "127.0.0.1";
    private ClientCommunicator communicator;
    private String[] adaptingBehaviors;

    public ClientServiceHandler(String[] adaptingBehaviors) throws IOException {
        socket = new Socket(SERVER_IP, SERVER_PORT);
        communicator = new ClientCommunicator(socket);
        this.adaptingBehaviors = adaptingBehaviors;
    }

    public void run(){
        int ITERATION = 3;
        int N = 10000;
        int loop = 0;
        String msg;
        boolean isFailed = false;

        while(loop<ITERATION) {
            //Send Adapt
            if(!isAdaptable(adaptingBehaviors[loop])) return;

            communicator.send(Command.GET);
            for (int i = 0; i < N; i++) {
                msg = communicator.receive();
//                System.out.println(msg);
                if(msg.contains(Command.ROLLBACK)){
                    isFailed = true;
                    break;
                }
            }
            if(!isFailed) loop++;
        }
        communicator.send(Command.QUIT);
    }

    private boolean isAdaptable(String operation){
        communicator.send(Command.ADAPT + ":" + operation);
        String resp = communicator.receive();
        return resp.contains(Command.ADAPT_OK);
    }
}
