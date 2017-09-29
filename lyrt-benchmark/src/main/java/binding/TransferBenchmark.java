package binding;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import binding.specimen.R2;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/1/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class TransferBenchmark {

    @Param({"0", "1", "2", "3", "4"})
    public int ARGS;
    private static int params[] = new int[]{10, 100, 1000, 5000, 10000};

    @State(Scope.Thread)
    public static class MyState {
        public Compartment[] compartments;
        public CoreObject[][] fromCoreObjects;
        public CoreObject[][] toCoreObjects;

        Registry reg = Registry.getRegistry();

        @Setup(Level.Invocation)
        public void setup() {
            int NUM = params.length;

            compartments = new Compartment[NUM];
            fromCoreObjects = new CoreObject[NUM][];
            toCoreObjects = new CoreObject[NUM][];

            for (int i = 0; i < NUM; i++) {
                compartments[i] = reg.newCompartment(Compartment.class);
//                int amount = (int) Math.pow(10, i + 1);
                int amount = params[i];
                fromCoreObjects[i] = new CoreObject[amount];

                BindingBenchmark.makeBinding(compartments[i], fromCoreObjects[i], amount);

                //initialize to cores
                toCoreObjects[i] = new CoreObject[amount];
                for (int j = 0; j < amount; j++) {
                    toCoreObjects[i][j] = reg.newCore(CoreObject.class);
                }


            }
        }
    }

    public static void transfer(Compartment compartment, CoreObject[] fromCores, CoreObject[] toCores){
        compartment.activate();
        for(int i=0; i<fromCores.length; i++){
            fromCores[i].transfer(R1.class, toCores[i]);
        }
        compartment.deactivate(false);
    }

    @Benchmark
    public void transfer(MyState s){
        transfer(s.compartments[ARGS], s.fromCoreObjects[ARGS], s.toCoreObjects[ARGS]);
    }

//    @Benchmark
//    public void transfer10(MyState s){
//        transfer(s.compartments[0], s.fromCoreObjects[0], s.toCoreObjects[0]);
//    }
//
//    @Benchmark
//    public void transfer100(MyState s){
//        transfer(s.compartments[1], s.fromCoreObjects[1], s.toCoreObjects[1]);
//    }
//
//    @Benchmark
//    public void transfer1000(MyState s){
//        transfer(s.compartments[2], s.fromCoreObjects[2], s.toCoreObjects[2]);
//    }
//
//    @Benchmark
//    public void transfer10000(MyState s){
//        transfer(s.compartments[3], s.fromCoreObjects[3], s.toCoreObjects[3]);
//    }
}
