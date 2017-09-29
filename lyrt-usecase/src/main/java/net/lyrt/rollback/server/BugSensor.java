package net.lyrt.rollback.server;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.rollback.ControlUnit;

import java.net.Socket;

/**
 * Created by nguonly on 6/27/17.
 */
public class BugSensor implements Thread.UncaughtExceptionHandler {
    private Socket client;

    public BugSensor(Socket client){
        this.client = client;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t + " Throwable: " + e);
        System.out.println("Thread status: " + t.getState());

        //Registry component in LyRT
        Registry reg = Registry.getRegistry();
        Compartment comp = reg.getActiveCompartments().get(t.getId());

        //Rollback
        ControlUnit controlUnit = new ControlUnit();
        controlUnit.rollback();

    }
}
