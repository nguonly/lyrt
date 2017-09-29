package binding;

import binding.specimen.CoreObject;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.rollback.ControlUnit;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Rollback5000Benchmark {
    @State(Scope.Thread)
    public static class MyState{

        public CoreObject[] coreObjects;
        public Compartment compartment;

        public Registry reg = Registry.getRegistry();

        public ControlUnit controlUnit = new ControlUnit();

        @Setup(Level.Invocation)
        public void setUp(){
            BindingBenchmark binding = new BindingBenchmark();

            compartment = reg.newCompartment(Compartment.class);
            coreObjects = new CoreObject[5000];
            binding.makeBinding(compartment, coreObjects, 5000);

//            System.out.println(compartment.getRelations().size());
            controlUnit.checkpoint(compartment);
        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            reg.clearCheckpoint();
            //System.out.println("tearDown");
        }
    }

    @Benchmark
    public void rollback5000(MyState state){
        state.controlUnit.rollback(state.compartment);
    }
}
