package binding;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.ConsistencyBlock;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ConsistencyBlockBenchmark {

    @State(Scope.Thread)
    public static class MyState{
        public Compartment compartment;

        ConsistencyBlock consistencyBlock;

        Registry reg = Registry.getRegistry();

        @Setup(Level.Invocation)
        public void setup(){
            compartment = reg.newCompartment(Compartment.class);
            compartment.activate();
        }
    }

    @Benchmark
    public void enteringConsistencyBlock(MyState s){
        s.consistencyBlock = new ConsistencyBlock();
        s.consistencyBlock.close();
    }
}
