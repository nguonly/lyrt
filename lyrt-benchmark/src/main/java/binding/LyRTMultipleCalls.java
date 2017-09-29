package binding;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LyRTMultipleCalls {

    @Param({"1000", "10000", "100000", "1000000", "10000000"})
    private int ARGS;

    @State(Scope.Thread)
    public static class MyState{
        private CoreObject core;
        private Compartment compartment;

        @Setup(Level.Invocation)
        public void setup(){
            Registry reg = Registry.getRegistry();

            compartment = reg.newCompartment(Compartment.class);
            core = reg.newCore(CoreObject.class);

            compartment.activate();
            core.bind(R1.class);
        }
    }

    @Benchmark
    public void invocationWithOneRole(MyState s, Blackhole blackhole){
        for(int i=0; i<ARGS; i++) {
            blackhole.consume(s.core.invoke("method", String.class, ""));
        }
    }
}
