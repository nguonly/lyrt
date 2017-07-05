package net.lyrt.block;

import net.lyrt.Compartment;

/**
 * Created by nguonly on 6/14/17.
 */
public class InitBindingBlock implements AutoCloseable{
    Compartment _compartment;

    public InitBindingBlock(Compartment compartment){
//        System.out.println("InitBindingBlock begins...");
        _compartment = compartment;
        _compartment.activate();
    }

    @Override
    public void close() {
//        System.out.println("InitBindingBlock ends...");
        //No need to do check point
        _compartment.deactivate(false);
    }
}
