package binding;

import binding.specimen.CoreObject;
import binding.specimen.delegation.*;
import binding.specimen.lowering.*;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/3/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LoweringBenchmark {

    @State(Scope.Thread)
    public static class MyState{
        public CoreObject[] coreObject;
        public Compartment compartment;

        Registry reg = Registry.getRegistry();

        public String stringValue = "";

        //Delegation
        public D1[] ds1;
        public D2[] ds2;
        public D3[] ds3;
        public D4[] ds4;
        public D5[] ds5;

        public CoreDelegation[] coreDelegation;

        @Setup(Level.Invocation)
        public void setup(){
            int NUM = 6;
            coreObject = new CoreObject[NUM];
            compartment = reg.newCompartment(Compartment.class);
            for(int i=0; i<NUM; i++) {
                coreObject[i] = reg.newCore(CoreObject.class);
            }

            try(InitBindingBlock ib = compartment.initBinding()){
                coreObject[1].bind(LoweringRole01.class);
                coreObject[2].bind(LoweringRole01.class).bind(LoweringRole02.class);
                coreObject[3].bind(LoweringRole01.class).bind(LoweringRole02.class).bind(LoweringRole03.class);
                coreObject[4].bind(LoweringRole01.class).bind(LoweringRole02.class).bind(LoweringRole03.class).bind(LoweringRole04.class);
                coreObject[5].bind(LoweringRole01.class).bind(LoweringRole02.class).bind(LoweringRole03.class).bind(LoweringRole04.class).bind(LoweringRole05.class);
            }

            setupDelegation(NUM);

            compartment.activate();
        }

        public void setupDelegation(int num){
            coreDelegation =  new CoreDelegation[num];
            ds1 = new D1[num];
            ds2 = new D2[num];
            ds3 = new D3[num];
            ds4 = new D4[num];
            ds5 = new D5[num];

            for(int i=0; i<num; i++){
                coreDelegation[i] = new CoreDelegation();
                ds1[i] = new D1();
                ds2[i] = new D2();
                ds3[i] = new D3();
                ds4[i] = new D4();
                ds5[i] = new D5();
            }

            //1 lowering
            coreDelegation[1].setLifting(ds1[1]);
            ds1[1].setLower(coreDelegation[1]);

            //2 lowering
            coreDelegation[2].setLifting(ds2[2]);
            ds2[2].setLower(ds1[2]);
            ds1[2].setLower(coreDelegation[2]);

            //3 lowering
            coreDelegation[3].setLifting(ds3[3]);
            ds3[3].setLower(ds2[3]);
            ds2[3].setLower(ds1[3]);
            ds1[3].setLower(coreDelegation[3]);


            //4 lowering
            coreDelegation[4].setLifting(ds4[4]);
            ds4[4].setLower(ds3[4]);
            ds3[4].setLower(ds2[4]);
            ds2[4].setLower(ds1[4]);
            ds1[4].setLower(coreDelegation[4]);

            //5 lowering
            coreDelegation[5].setLifting(ds5[5]);
            ds5[5].setLower(ds4[5]);
            ds4[5].setLower(ds3[5]);
            ds3[5].setLower(ds2[5]);
            ds2[5].setLower(ds1[5]);
            ds1[5].setLower(coreDelegation[5]);
        }
    }

    @Benchmark
    public void invocationOnLowering0(MyState s, Blackhole b){
        b.consume(s.coreObject[0].invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void invocationOnLowering1(MyState s, Blackhole b){
        b.consume(s.coreObject[1].invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void invocationOnLowering2(MyState s, Blackhole b){
        b.consume(s.coreObject[2].invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void invocationOnLowering3(MyState s, Blackhole b){
        b.consume(s.coreObject[3].invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void invocationOnLowering4(MyState s, Blackhole b){
        b.consume(s.coreObject[4].invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void invocationOnLowering5(MyState s, Blackhole b){
        b.consume(s.coreObject[5].invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void loweringDelegation0(MyState s, Blackhole b){
        b.consume(s.coreDelegation[0].liftMethod(s.stringValue));
    }

    @Benchmark
    public void loweringDelegation1(MyState s, Blackhole b){
        b.consume(s.coreDelegation[1].liftMethod(s.stringValue));
    }

    @Benchmark
    public void loweringDelegation2(MyState s, Blackhole b){
        b.consume(s.coreDelegation[2].liftMethod(s.stringValue));
    }

    @Benchmark
    public void loweringDelegation3(MyState s, Blackhole b){
        b.consume(s.coreDelegation[3].liftMethod(s.stringValue));
    }

    @Benchmark
    public void loweringDelegation4(MyState s, Blackhole b){
        b.consume(s.coreDelegation[4].liftMethod(s.stringValue));
    }

    @Benchmark
    public void loweringDelegation5(MyState s, Blackhole b){
        b.consume(s.coreDelegation[5].liftMethod(s.stringValue));
    }
}
