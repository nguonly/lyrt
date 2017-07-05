package binding;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.rollback.ControlUnit;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/30/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Rollback10Benchmark {


    @State(Scope.Thread)
    public static class MyState{

        public CoreObject[] coreObjects;
        public Compartment compartment;

//        public CoreObject[][] coreObjects;
//
//        public Compartment[] compartments;

        public Registry reg = Registry.getRegistry();

        public ControlUnit controlUnit = new ControlUnit();

        @Setup(Level.Invocation)
        public void setUp(){
            BindingBenchmark binding = new BindingBenchmark();

            compartment = reg.newCompartment(Compartment.class);
            coreObjects = new CoreObject[10];
            binding.makeBinding(compartment, coreObjects, 10);

            controlUnit.checkpoint(compartment);
        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            reg.clearCheckpoint();
            //System.out.println("tearDown");
        }
    }

    @Benchmark
    public void rollback10(MyState state){
        state.controlUnit.rollback(state.compartment);
    }

}
