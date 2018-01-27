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
                ServiceHandler handler = new ServiceHandler(client, tf);
                handler.setName(clientName);
                handler.start();
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
            AppState.listClient.getSelectedValuesList().forEach(k -> k.disconnect());

            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adapt(String adaptation){
        AppState.listClient.getSelectedValuesList().forEach(k -> k.processAdaptation(adaptation));
    }

    public void resetAdaptation(){
        AppState.listClient.getSelectedValuesList().forEach(k -> k.resetAdaptation());
    }

    public void restartService(ServiceHandler clientHandler){
        //remove existing socket from list
        AppState.listModel.removeElement(clientHandler);
        clientHandler.recProp.isRecovered = true;
        ServiceHandler handler = new ServiceHandler(clientHandler.socket, clientHandler.recProp);
        handler.start();
        AppState.listModel.addElement(handler);
    }
}
