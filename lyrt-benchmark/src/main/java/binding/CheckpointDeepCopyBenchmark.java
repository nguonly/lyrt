package binding;

import binding.specimen.CoreObject;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.rollback.ControlUnit;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/4/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CheckpointDeepCopyBenchmark {

    @State(Scope.Thread)
    public static class MyState{
        private CoreObject[][] coreObjects;
        private Compartment[] compartments;

        Registry reg = Registry.getRegistry();
        ControlUnit controlUnit = new ControlUnit();
        @Setup(Level.Invocation)
        public void setup(){
            int NUM = 4; //10, 100, 1000, 10000
            coreObjects = new CoreObject[NUM][];
            compartments = new Compartment[NUM];

            for(int i=0; i<NUM; i++){
                compartments[i] = reg.newCompartment(Compartment.class);

                int amount = (int)Math.pow(10, i+1);

                coreObjects[i] = new CoreObject[amount];

                BindingBenchmark.makeBinding(compartments[i], coreObjects[i], amount);
            }

            //use deep copy as a checkpoint
            reg.setCheckpointMode(true);
        }

        @TearDown
        public void tearDown(){
            reg.clearCheckpoint();
        }
    }

    @Benchmark
    public void checkpointDeepCopy10(MyState s){
        s.controlUnit.checkpoint(s.compartments[0]);
    }

    @Benchmark
    public void checkpointDeepCopy100(MyState s){
        s.controlUnit.checkpoint(s.compartments[1]);
    }

    @Benchmark
    public void checkpointDeepCopy1000(MyState s){
        s.controlUnit.checkpoint(s.compartments[2]);
    }

    @Benchmark
    public void checkpointDeepCopy10000(MyState s){
        s.controlUnit.checkpoint(s.compartments[3]);
    }
}
