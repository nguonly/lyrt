package rewriting;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import rewriting.specimen.Foo;

import java.lang.instrument.ClassDefinition;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JavassistAdaptationBenchmark {

    public static void main(String... args){
        Foo f1 = new Foo();
        Foo f2 = new Foo();

        f1.method("a");
        f2.method("b");

        MyState s = new MyState();
        s.redefine(1);

        f1.method("aa");
        f2.method("bb");
    }

    @Param({"0", "1", "2", "3"})
    public int ARGS; //align with params_invoking

    //private static int[] params = new int[]{10, 100, 1000, 5000, 10000};
    private static int[] params = new int[]{10, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000};

    private static int[] params_invoking = new int[]{1000, 10000, 100000, 1000000};

    @State(Scope.Thread)
    public static class MyState{
        private Foo[][] foos;
        private String stringValue = "";

        @Setup(Level.Invocation)
        public void setup(){
            int NUM = params.length;

            foos = new Foo[NUM][];

            for(int i=0; i<NUM; i++) {
//                int numCore = (int)Math.pow(10, i+1);
                int numCore = params[i];
                foos[i] = new Foo[numCore];

                for(int j=0; j<numCore; j++){
                    foos[i][j] = new Foo();
                }
            }
        }

        private void redefine(int numBinding){
            try{
                ClassPool cp = ClassPool.getDefault();
                for(int i=0; i<numBinding; i++) {
                    CtClass cc = cp.get("rewriting.specimen.Foo");
                    if(cc.isFrozen()) cc.defrost();

                    CtMethod method = cc.getDeclaredMethod("method");

//                  method.insertBefore("{ System.out.println(\"Hello.say():\"); }");
//                    method.setBody("{System.out.println(\"new method def " + i + "\"); return $1;}"); //$1 is the first param
                    method.setBody("{return $1;}"); //$1 is the first param
//                    cc.writeFile();
                    byte[] bytecode = cc.toBytecode();

                    ClassDefinition definition = new ClassDefinition(Class.forName("rewriting.specimen.Foo"), bytecode);
                    RedefineClassAgent.redefineClasses(definition);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void redefine(Blackhole bh, Foo[] foos, int numBinding, int numInvoking) {
            redefine(numBinding);

            invoke(bh, foos, numBinding, numInvoking);
        }

        private void invoke(Blackhole blackhole, Foo[] foos, int numBinding, int numInvoking){
            if(numBinding < numInvoking) {
//                int iterations = (int) Math.log(numInvoking) / (int) Math.log(10);
                int iterations = numInvoking / numBinding;
                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < numBinding; j++) {
                        blackhole.consume(foos[j].method(stringValue));
                    }
                }
            }else{
                for (int j = 0; j < numInvoking; j++) {
                    blackhole.consume(foos[j].method(stringValue));
                }
            }
//            for(int i=0; i<num; i++){
//                blackhole.consume(foo.method(stringValue));
//            }
        }
    }

    @Benchmark
    public void adapt10(Blackhole blackhole, MyState s){
        int i=0;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);

    }

    @Benchmark
    public void adapt100(Blackhole blackhole, MyState s){
        int i=1;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt200(Blackhole blackhole, MyState s){
        int i=2;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt300(Blackhole blackhole, MyState s){
        int i=3;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt400(Blackhole blackhole, MyState s){
        int i=4;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt500(Blackhole blackhole, MyState s){
        int i=5;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt600(Blackhole blackhole, MyState s){
        int i=6;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt700(Blackhole blackhole, MyState s){
        int i=7;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt800(Blackhole blackhole, MyState s){
        int i=8;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt900(Blackhole blackhole, MyState s){
        int i=9;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt1000(Blackhole blackhole, MyState s){
        int i=10;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt2000(Blackhole blackhole, MyState s){
        int i=11;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

    @Benchmark
    public void adapt3000(Blackhole blackhole, MyState s){
        int i=12;
        s.redefine(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
    }

//    @Benchmark
//    public void adapt1000(Blackhole blackhole, MyState s){
//        s.redefine(blackhole, s.foos[2],1000, params_invoking[ARGS]);
//    }
//
//    @Benchmark
//    public void adapt5000(Blackhole blackhole, MyState s){
//        s.redefine(blackhole, s.foos[3],5000, params_invoking[ARGS]);
//    }
//
//    @Benchmark
//    public void adapt10000(Blackhole blackhole, MyState s){
//        s.redefine(blackhole, s.foos[4],10000, params_invoking[ARGS]);
//    }

    @Benchmark
    public void invokeInAdapt1000ManyTimes(Blackhole blackhole, MyState s){
        int i=0;
        s.invoke(blackhole, s.foos[i], params[i], params_invoking[ARGS]);
//        for(int i=0; i<params_invoking[ARGS]; i++){
//            blackhole.consume(s.foos[0][0].method(""));
//        }
    }

//    @Benchmark
//    public void invokeInAdapt100(Blackhole blackhole, MyState s){
//        s.invoke(blackhole, s.foos[1], 100, params_invoking[ARGS]);
//    }
//
//    @Benchmark
//    public void invokeInAdapt1000(Blackhole blackhole, MyState s){
//        s.invoke(blackhole, s.foos[2], 1000, params_invoking[ARGS]);
//    }
}
