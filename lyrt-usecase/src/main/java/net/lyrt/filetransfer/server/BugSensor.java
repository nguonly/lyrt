package net.lyrt.filetransfer.server;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.helper.DumpHelper;
import net.lyrt.rollback.ControlUnit;

import java.net.Socket;

public class BugSensor implements Thread.UncaughtExceptionHandler {
//    private Socket socket;
    private ServiceHandler clientHandler;

    public BugSensor(ServiceHandler client){
        this.clientHandler = client;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t + " Throwable: " + e);
        System.out.println("Thread status: " + t.getState());

        //Registry component in LyRT
        Registry reg = Registry.getRegistry();
        Compartment comp = reg.getActiveCompartments().get(t.getId());

        DumpHelper.dumpRelations(comp);

        System.out.println(">> Rollback >> Current Thread : " + Thread.currentThread().getId());
        System.out.println(">> Rollback >> Error Thread : " + t.getId());
        //Rollback
        ControlUnit cu= new ControlUnit();
        cu.rollback(t.getId(), comp);

        //Logging
        System.out.println("Rollback >>> in Compartment : " + comp.hashCode());
        AppState.appendMessage (t + " throws " + e);
        AppState.appendMessage(t.getName() + ">>>>> Rollback >>>> ");
        DumpHelper.dumpRelations(comp);

        //Restart the socket thread
        AppState.serverService.restartService(clientHandler);
    }
}
