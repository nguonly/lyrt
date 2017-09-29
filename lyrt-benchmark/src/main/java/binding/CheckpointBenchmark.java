package binding;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import com.esotericsoftware.kryo.Kryo;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.rollback.ControlUnit;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/30/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CheckpointBenchmark {

    @State(Scope.Thread)
    public static class MyState{
        public CoreObject[] coreObjects10;
        public CoreObject[] coreObjects100;
        public CoreObject[] coreObjects1000;
        public CoreObject[] coreObjects5000;
        public CoreObject[] coreObjects10000;

        public Compartment compartment10;
        public Compartment compartment100;
        public Compartment compartment1000;
        public Compartment compartment5000;
        public Compartment compartment10000;

        public Registry reg = Registry.getRegistry();

        public ControlUnit controlUnit = new ControlUnit();

        @Setup(Level.Invocation)
        public void setUp(){

            compartment10 = reg.newCompartment(Compartment.class);
            compartment100 = reg.newCompartment(Compartment.class);
            compartment1000 = reg.newCompartment(Compartment.class);
            compartment5000 = reg.newCompartment(Compartment.class);
            compartment10000 = reg.newCompartment(Compartment.class);

            makeBinding(compartment10, coreObjects10, 10);
            makeBinding(compartment100, coreObjects100, 100);
            makeBinding(compartment1000, coreObjects1000, 1000);
            makeBinding(compartment5000, coreObjects5000, 5000);
            makeBinding(compartment10000, coreObjects10000, 10000);

        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            reg.clearCheckpoint();
            //System.out.println("tearDown");
        }

        private void makeBinding(Compartment compartment, CoreObject[] cores, int amount){
            cores = new CoreObject[amount];
            //System.out.println(coreObjects.length);
            for(int i=0; i<amount; i++){
                cores[i] = reg.newCore(CoreObject.class);
            }

            try(InitBindingBlock ib = compartment.initBinding()){
                for(int i=0; i<amount; i++){
                    cores[i].bind(R1.class);
                }
            }
        }
    }

    @Benchmark
    public void checkPoint10(MyState state){
        state.controlUnit.checkpoint(state.compartment10);
    }

    @Benchmark
    public void checkPoint100(MyState state){
        state.controlUnit.checkpoint(state.compartment100);
    }

    @Benchmark
    public void checkPoint1000(MyState state){
        state.controlUnit.checkpoint(state.compartment1000);
    }

    @Benchmark
    public void checkPoint5000(MyState state){
        state.controlUnit.checkpoint(state.compartment5000);
    }

    @Benchmark
    public void checkPoint10000(MyState state){
        state.controlUnit.checkpoint(state.compartment10000);
    }
//
//    @Benchmark
//    public void copyRelation10(MyState state){
//        state.controlUnit.copyRelations(state.compartment10);
//    }
//
}
