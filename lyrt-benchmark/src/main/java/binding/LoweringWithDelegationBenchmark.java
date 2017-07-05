package binding;

import binding.specimen.CoreObject;
import binding.specimen.delegation.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/4/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LoweringWithDelegationBenchmark {

    public static void main(String... args){
        MyState s = new MyState();
        s.setup();

        System.out.println(s.coreDelegation[0].liftMethod("a"));
        System.out.println(s.coreDelegation[1].liftMethod("a"));
        System.out.println(s.coreDelegation[2].liftMethod("a"));
        System.out.println(s.coreDelegation[3].liftMethod("a"));
        System.out.println(s.coreDelegation[4].liftMethod("a"));
        System.out.println(s.coreDelegation[5].liftMethod("a"));
    }

    @State(Scope.Thread)
    public static class MyState{
        public String stringValue = "";

        public D1[] ds1;
        public D2[] ds2;
        public D3[] ds3;
        public D4[] ds4;
        public D5[] ds5;

        public CoreDelegation[] coreDelegation;

        public CoreObject coreObject;

        @Setup(Level.Invocation)
        public void setup(){
            int NUM = 6;
            coreDelegation =  new CoreDelegation[NUM];
            ds1 = new D1[NUM];
            ds2 = new D2[NUM];
            ds3 = new D3[NUM];
            ds4 = new D4[NUM];
            ds5 = new D5[NUM];

            for(int i=0; i<NUM; i++){
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

            coreObject = new CoreObject();

        }
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

//    @Benchmark
//    public void staticInvocation(MyState s, Blackhole b){
////        b.consume(s.coreDelegation.method(s.stringValue));
////        s.coreDelegation.method(s.stringValue);
//    }
//
//    @Benchmark
//    public void staticInvocationOfCoreObject(MyState s, Blackhole b){
//        b.consume(s.coreObject.method(s.stringValue));
//        b.consume(s.coreObject.method(s.stringValue));
//        b.consume(s.coreObject.method(s.stringValue));
//        b.consume(s.coreObject.method(s.stringValue));
//        b.consume(s.coreObject.method(s.stringValue));
//    }
}
