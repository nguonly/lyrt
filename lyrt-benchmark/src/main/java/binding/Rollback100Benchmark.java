package binding;

import binding.specimen.CoreObject;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.rollback.ControlUnit;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/30/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Rollback100Benchmark {
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
            coreObjects = new CoreObject[100];
            binding.makeBinding(compartment, coreObjects, 100);

            controlUnit.checkpoint(compartment);
        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            reg.clearCheckpoint();
            //System.out.println("tearDown");
        }
    }

    @Benchmark
    public void rollback100(MyState state){
        state.controlUnit.rollback(state.compartment);
    }
}
