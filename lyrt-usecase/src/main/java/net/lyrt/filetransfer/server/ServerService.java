package net.lyrt.filetransfer.server;

import net.lyrt.Compartment;
import net.lyrt.Registry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Hashtable;

public class ServerService extends Thread{
    ServerSocket server;
    int PORT = 8888;
//    HashMap<Socket, ServiceHandler> listClient = new HashMap<>();
//    Hashtable<String, RecoveryProperty> lastKnownErrorChunks = new Hashtable<>();
//    String clientName;

    public void run(){
        try {
            server = new ServerSocket(PORT);

            while (true) {
                System.out.println("Waiting for client...");
                AppState.txtMsg.append("Waiting for client...\n");
                Socket client = server.accept();
                AppState.txtMsg.append("Got a connection from " + client.getInetAddress().toString() + " port:" + client.getPort() + "\n");

                String clientName = client.getInetAddress() + ":" + client.getPort();
                Compartment compartment = Registry.getRegistry().newCompartment(Compartment.class);
                RecoveryProperty tf = new RecoveryProperty();
                tf.compartment = compartment;
//                lastKnownErrorChunks.put(clientName, tf);
                ServiceHandler handler = new ServiceHandler(client, tf);
                handler.setName(clientName);
                handler.start();
//                listClient.put(client, handler);
                AppState.listModel.addElement(handler);
            }
        }catch(SocketException se){
            System.out.println("Server is shutdown");
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void stopServer(){
        try {
            //alert all connected clients to disconnect
//            listClient.forEach((k, v) -> v.disconnect());
            AppState.listClient.getSelectedValuesList().forEach(k -> k.disconnect());

            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adapt(String adaptation){
//        listClient.forEach((k, v) -> v.processAdaptation(adaptation));
        AppState.listClient.getSelectedValuesList().forEach(k -> k.processAdaptation(adaptation));
    }

    public void resetAdaptation(){
//        listClient.forEach((k, v) -> v.resetAdaptation());
        AppState.listClient.getSelectedValuesList().forEach(k -> k.resetAdaptation());
    }

    public void restartService(ServiceHandler clientHandler){
        //remove existing socket from list
//        String clientName = socket.getInetAddress() + ":" + socket.getPort();
//        listClient.remove(clientName);

        AppState.listModel.removeElement(clientHandler);
        clientHandler.recProp.isRecovered = true;
        ServiceHandler handler = new ServiceHandler(clientHandler.socket, clientHandler.recProp);
        handler.start();
//        listClient.put(clientHandler.socket, handler);
        AppState.listModel.addElement(handler);
    }
}
