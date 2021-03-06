package binding;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/30/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class UnbindingBenchmark {

    public static void main(String... args){
        MyState s = new MyState();

        s.setup();
        makeUnbinding(s.compartments[0], s.coreObjects[0]);
    }

    //@Param({"0", "1", "2", "3", "4"})
    @Param({"0", "1"})
    private int ARGS;

    private static int[] params = new int[]{10, 100, 1000, 5000, 10000};

    @State(Scope.Thread)
    public static class MyState{
        public Compartment[] compartments;
        public CoreObject[][] coreObjects;

        Registry reg = Registry.getRegistry();

        @Setup(Level.Invocation)
        public void setup(){
            int NUM = params.length;

            compartments = new Compartment[NUM];
            coreObjects = new CoreObject[NUM][];

            for(int i=0; i<NUM; i++){
                compartments[i] = reg.newCompartment(Compartment.class);
//                int amount = (int)Math.pow(10, i+1);
                int amount = params[i];
                coreObjects[i] = new CoreObject[amount];
                BindingBenchmark.makeBinding(compartments[i], coreObjects[i], amount);
            }
        }
    }

    public static void makeUnbinding(Compartment compartment, CoreObject[] cores){
        compartment.activate();
        for(int i=0; i<cores.length; i++){
            cores[i].unbind(R1.class);
        }
        compartment.deactivate(false);
    }

    @Benchmark
    public void unbinding(MyState s){
        makeUnbinding(s.compartments[ARGS], s.coreObjects[ARGS]);
    }

//    @Benchmark
//    public void unbinding10(MyState s){
//        makeUnbinding(s.compartments[0], s.coreObjects[0]);
//    }
//
//    @Benchmark
//    public void unbinding100(MyState s){
//        makeUnbinding(s.compartments[1], s.coreObjects[1]);
//    }
//
//    @Benchmark
//    public void unbinding1000(MyState s){
//        makeUnbinding(s.compartments[2], s.coreObjects[2]);
//    }
//
//    @Benchmark
//    public void unbinding10000(MyState s){
//        makeUnbinding(s.compartments[3], s.coreObjects[3]);
//    }
}
