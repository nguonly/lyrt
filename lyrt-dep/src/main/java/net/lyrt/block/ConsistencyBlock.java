package net.lyrt.block;

import net.lyrt.Compartment;
import net.lyrt.Registry;

import java.time.LocalDateTime;

/**
 * Created by nguonly on 6/20/17.
 */
public class ConsistencyBlock implements AutoCloseable {
    private Registry _reg;
    private Compartment _compartment;

    public ConsistencyBlock(){
        _reg = Registry.getRegistry();
        _compartment = _reg.getActiveCompartment();
        if(_compartment==null) throw new RuntimeException("No active compartment was found");

        LocalDateTime time = LocalDateTime.now();
        _reg.registerConsistencyBlock(_compartment, this.hashCode(), time);
    }

    @Override
    public void close() {
        _reg.removeConsistencyBlock(_compartment, this.hashCode());
    }
}
