package binding;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.ConsistencyBlock;
import net.lyrt.block.InitBindingBlock;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/2/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class WithConsistencyInvocationBenchmark {

    @State(Scope.Thread)
    public static class MyState{
        public CoreObject coreObject;
        public Compartment compartment;

        ConsistencyBlock consistencyBlock;

        private String stringValue = "";

        Registry reg = Registry.getRegistry();

        @Setup(Level.Invocation)
        public void setup(){
            compartment = reg.newCompartment(Compartment.class);

            coreObject = reg.newCore(CoreObject.class);

            try(InitBindingBlock ib = compartment.initBinding()){
                coreObject.bind(R1.class);
            }

            compartment.activate();
            consistencyBlock = new ConsistencyBlock();
        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            consistencyBlock.close();
        }
    }

    @Benchmark
    public void withConsistency(MyState s, Blackhole b){
        b.consume(s.coreObject.invoke("method", String.class, s.stringValue));
    }

}
