package net.lyrt.block;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.rollback.ControlUnit;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nguonly on 6/20/17.
 */
public class AdaptationBlock implements AutoCloseable{
    ReentrantLock lock = new ReentrantLock();
    public AdaptationBlock(){
        lock.lock();
        try {
            Registry reg = Registry.getRegistry();
            Compartment compartment = reg.getActiveCompartment();
            if (compartment == null) throw new RuntimeException("No active compartment was found");

            ControlUnit controlUnit = new ControlUnit();
            controlUnit.checkpoint(compartment);
            //ControlUnit.checkpoint(compartment);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void close(){

    }
}
