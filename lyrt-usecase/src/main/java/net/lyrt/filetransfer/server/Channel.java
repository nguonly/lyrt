package net.lyrt.filetransfer.server;

import net.lyrt.IPlayer;

public class Channel implements IPlayer {
//    private Communicator communicator;

//    public void setCommunicator(Communicator communicator){
//        this.communicator = communicator;
//    }

//    public void send(String data){
//        communicator.send(invoke("format", String.class, data));
//    }
//
//    public String receive(){
//        String msg = communicator.receive();
//        return msg;
////        return invoke("unformat", String.class, msg);
//    }

    public String format(String data){
        return data;
    }

    public String unformat(String data){
        return data;
    }

    public int factor(){ return 10;}
}
