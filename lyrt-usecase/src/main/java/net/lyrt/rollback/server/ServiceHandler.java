package net.lyrt.rollback.server;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.AdaptationBlock;
import net.lyrt.rollback.roles.AES;
import net.lyrt.rollback.roles.LZ;
import net.lyrt.rollback.roles.LZX;

import java.net.Socket;

/**
 * Created by nguonly on 6/27/17.
 */
public class ServiceHandler extends Thread{
    private Socket client;
    private Compartment compartment;
    private Communicator communicator;
    private Channel channel;

    public ServiceHandler(Socket socket){
        this.client = socket;
        Registry reg = Registry.getRegistry();
        compartment = reg.newCompartment(Compartment.class);
        channel = reg.newCore(Channel.class);

        communicator = new Communicator(client, channel);

    }

    public void run(){
        compartment.activate();
        System.out.println("CompartmentId : " + compartment.hashCode());
        while(true){
            String msg = communicator.receive();

            if(msg.equals(Command.QUIT)){
                compartment.deactivate(false);
                break;
            }else if(msg.contains(Command.ADAPT)){
                String[] commands = msg.split(":");
                if(commands[0].equals(Command.ADAPT)) {
                    processAdaptation(commands[1]);

                    communicator.send(Command.ADAPT_OK);
                }
            }else if(msg.contains(Command.GET)){
                processReceiving();
            }

        }
    }

    private void processReceiving(){
        int N = 10000;
        for(int i=0; i<N; i++){
            communicator.send("DATA");
        }
    }

    private void processAdaptation(String operation){

        try(AdaptationBlock ab = new AdaptationBlock()){
            switch (operation){
                case "AES":
                    channel.bind(AES.class);
                    break;
                case "LZ":
                    channel.bind(LZ.class);
                    break;
                case "LZX":
                    channel.bind(LZX.class);
                    break;
                case "LZ-AES":
                    channel.bind(LZ.class).bind(AES.class);
                    break;
                case "LZX-AES":
                    channel.bind(LZX.class).bind(AES.class);
                    break;
                case "AES-LZ":
                    channel.bind(AES.class).bind(LZ.class);
                    break;
                case "AES-LZX":
                    channel.bind(AES.class).bind(LZX.class);
                    break;
            }
        }
    }
}
