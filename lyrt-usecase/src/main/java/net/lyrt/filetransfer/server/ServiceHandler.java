package net.lyrt.filetransfer.server;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.AdaptationBlock;
import net.lyrt.block.ConsistencyBlock;
import net.lyrt.filetransfer.server.role.AES;
import net.lyrt.filetransfer.server.role.LZ;

import java.net.Socket;
import java.util.Hashtable;

public class ServiceHandler extends Thread{
    Socket socket;
    Compartment compartment;
    Communicator communicator;
    Channel channel;
    RecoveryProperty recProp = new RecoveryProperty();

    public ServiceHandler(Socket socket){
        this.socket = socket;
//        Registry reg = Registry.getRegistry();
//        reg.setCheckpointMode(true);
//        channel = reg.newCore(Channel.class);
        channel = AppState.channel;
        communicator = new Communicator(this.socket, channel);
//        channel.setCommunicator(communicator);
    }

    public ServiceHandler(Socket socket, RecoveryProperty recoveryProperty){
        this(socket);
        this.recProp = recoveryProperty;
        this.compartment = recProp.compartment;
    }

    public void run(){
        //install the bug sensor
        Thread.currentThread().setUncaughtExceptionHandler(new BugSensor(this));

        compartment.activate();
        System.out.println("CompartmentId : " + compartment.hashCode() + "  Channel : " + channel.hashCode());
        System.out.println("Thread Id: " + Thread.currentThread().getId());
        while(true){
            if(recProp.isRecovered) {
                resumeSendingFileChunks();
                recProp.isRecovered=false;
            }
            String msg = communicator.receive();
            System.out.println(msg);
            if(msg.equals(Command.QUIT)){
                compartment.deactivate(false);
                AppState.listModel.removeElement(this);
                break;
            }else if(msg.contains(Command.GET)){
                String[] com = msg.split(" ");
                int n = Integer.parseInt(com[1]);
                recProp.numberOfChunks = n;
                if(AppState.isTranquil)
                    try(ConsistencyBlock cb = new ConsistencyBlock()) {
                        sendFileChunks(n);
                    }
                else sendFileChunks(n);
            }
        }
    }

    private void sendFileChunks(int startIdx, int n){
        for(int i=startIdx; i<n; i++){
            delay(500);
            communicator.send("DATA " + (i+1)); //simulate a file chunk
            recProp.offset = i;
        }
    }

    private void sendFileChunks(int n){
        sendFileChunks(0, n);
    }

    private void resumeSendingFileChunks(){
        sendFileChunks(recProp.offset+1, recProp.numberOfChunks);
    }

    private void delay(int milli){
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        communicator.send(Command.QUIT);
    }

    public void processAdaptation(String operation){
        compartment.activate();
//        channel.unbind(LZ.class);
//        channel.unbind(AES.class);
        System.out.println("is unanticipated : " + Registry.getRegistry().isUnanticipated);
        try(AdaptationBlock ab = new AdaptationBlock()){
            if(operation.equals("AES")) channel.bind(AES.class);
            if(operation.equals("LZ")) channel.bind(LZ.class);
            if(operation.equals("LZ-AES")) channel.bind(LZ.class).bind(AES.class);
            if(operation.equals("AES-LZ")) channel.bind(AES.class).bind(LZ.class);
//            switch (operation){
//                case "AES":
//                    channel.bind(AES.class);
//                    break;
//                case "LZ":
//                    channel.bind(LZ.class);
//                    break;
//                case "LZ-AES":
//                    channel.bind(LZ.class).bind(AES.class);
//                    break;
//                case "AES-LZ":
//                    channel.bind(AES.class).bind(LZ.class);
//                    break;
//            }
        }

        System.out.println("In ProcessAdaptation Thread : " + Thread.currentThread().getId() + "  compartment : " + compartment.hashCode());
//        DumpHelper.dumpRelations(compartment);
//        DumpHelper.dumpStacks(compartment);
        System.out.println("-------------------------");

    }

    public void resetAdaptation(){
        compartment.activate();
        try(AdaptationBlock ab = new AdaptationBlock()){
            channel.unbind(LZ.class);
            channel.unbind(AES.class);
        }
    }

    public String toString(){
        return this.socket.getInetAddress() + ":" + this.socket.getPort();
    }
}
