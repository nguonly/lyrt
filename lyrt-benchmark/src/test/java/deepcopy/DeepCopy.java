package deepcopy;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.rollback.ControlUnit;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by nguonly on 7/4/17.
 */
public class DeepCopy {

    private CoreObject[] coreObjects;

    private Compartment compartment;

    private ControlUnit controlUnit = new ControlUnit();

    Registry reg = Registry.getRegistry();
    public void binding(int num){
        coreObjects = new CoreObject[num];
        compartment = reg.newCompartment(Compartment.class);

        try(InitBindingBlock ib = compartment.initBinding()) {
            for (int i = 0; i < num; i++) {
                coreObjects[i] = reg.newCore(CoreObject.class);
                coreObjects[i].bind(R1.class);
            }
        }

        reg.setCheckpointMode(true);
    }

    public void deepcopy(){
        controlUnit.checkpoint(compartment);
    }

    public static void main(String... args){
        int num = 1000;
        DeepCopy deepCopy = new DeepCopy();
        deepCopy.binding(num);

        Instant start = Instant.now();
        for(int i=0; i<1_000; i++){
            deepCopy.deepcopy();
        }
        Instant end = Instant.now();

        Duration taken = Duration.between(start, end);
        System.out.println(String.format("Time taken for %d roles is %d", num, taken.toMillis()));
    }
}
