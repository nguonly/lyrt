package adaptation;

import binding.BindingBenchmark;
import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LyRTAdaptationBenchmark {

    private static String stringValue = "";

    @Param({"0", "1", "2", "3"})
    public int ARGS; //align with params_invoking

    //private static int[] params = new int[]{10, 100, 1000, 5000, 10000};
    private static int[] params = new int[]{10, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000};

    private static int[] params_invoking = new int[]{1000, 10000, 100000, 1000000};

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
//                int numCore = (int)Math.pow(10, i+1);
                int numCore = params[i];
                coreObjects[i] = new CoreObject[numCore];

                for(int j=0; j<numCore; j++){
                    coreObjects[i][j] = reg.newCore(CoreObject.class);
                }

            }
        }
    }

    public static void makeBinding(Blackhole blackhole, Compartment compartment, CoreObject[] cores, int numBinding, int numInvoking){
//        Registry reg = Registry.getRegistry();

//        cores = new CoreObject[amount];
//            System.out.println(cores.length);
//        for(int i=0; i<numBinding; i++){
//            cores[i] = reg.newCore(CoreObject.class);
//        }
        compartment.activate();
        try(InitBindingBlock ib = compartment.initBinding()){
            for(int i=0; i<numBinding; i++){
                cores[i].bind(R1.class);
            }
        }

        if(numBinding < numInvoking) {
//            int iterations = (int) Math.log(numInvoking) / (int) Math.log(10);
            int iterations = numInvoking / numBinding;
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < numBinding; j++) {
                    blackhole.consume(cores[j].invoke("method", String.class, stringValue));
                }
            }
        }else{
            for (int j = 0; j < numInvoking; j++) {
                blackhole.consume(cores[j].invoke("method", String.class, stringValue));
            }
        }

//        makeInvoking();
    }

//    public static void makeInvoking(Blackhole blackhole, CoreObject core, int amount){
//        for(int i=0; i<amount; i++) {
//            blackhole.consume(core.invoke("method", String.class, stringValue));
//        }
//    }

    @Benchmark
    public void adapt10(MyState state, Blackhole blackhole){
        int i=0;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt100(MyState state, Blackhole blackhole){
        int i=1;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

//    @Benchmark
//    public void adapt1000(MyState state, Blackhole blackhole){
//        makeBinding(blackhole, state.compartments[2], state.coreObjects[2], 1000, params_invoking[ARGS]);
//    }
//
//    @Benchmark
//    public void adapt5000(MyState state, Blackhole blackhole){
//        makeBinding(blackhole, state.compartments[3], state.coreObjects[3], 5000, params_invoking[ARGS]);
//    }
//
//    @Benchmark
//    public void adapt10000(MyState state, Blackhole blackhole){
//        makeBinding(blackhole, state.compartments[4], state.coreObjects[4], 10000, params_invoking[ARGS]);
//    }

    /* Extra benchmarks for 3D plot */

    @Benchmark
    public void adapt200(MyState state, Blackhole blackhole){
        int i=2;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt300(MyState state, Blackhole blackhole){
        int i=3;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt400(MyState state, Blackhole blackhole){
        int i=4;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt500(MyState state, Blackhole blackhole){
        int i=5;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt600(MyState state, Blackhole blackhole){
        int i=6;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt700(MyState state, Blackhole blackhole){
        int i=7;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt800(MyState state, Blackhole blackhole){
        int i=8;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt900(MyState state, Blackhole blackhole){
        int i=9;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt1000(MyState state, Blackhole blackhole){
        int i=10;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt2000(MyState state, Blackhole blackhole){
        int i=11;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt3000(MyState state, Blackhole blackhole){
        int i=12;
        makeBinding(blackhole, state.compartments[i], state.coreObjects[i], params[i], params_invoking[ARGS]);
    }
}
