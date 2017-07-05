package rewriting;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.openjdk.jmh.annotations.*;

import java.lang.instrument.ClassDefinition;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/1/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JavassistRedefinitionBenchmark {

    public static void main(String... args){
        Foo foo = new Foo();
        Foo foo1 = new Foo();
        foo.method();
        foo1.method();

        MyState s = new MyState();
        s.redefine(3);
        foo.method();
        foo1.method();
    }

    public static class Bar {
        void method() { System.out.println("bar"); }
    }

    interface IFoo{
        void method();
    }

    public static class Foo implements IFoo{
        public void method() { System.out.println("foo"); }
    }

    @State(Scope.Thread)
    public static class MyState{

        private void redefine(int num) {
            try{
                ClassPool cp = ClassPool.getDefault();
                for(int i=0; i<num; i++) {
                    CtClass cc = cp.get("rewriting.JavassistRedefinitionBenchmark$Foo");
                    if(cc.isFrozen()) cc.defrost();

                    CtMethod method = cc.getDeclaredMethod("method");

                    //            method.insertBefore("{ System.out.println(\"Hello.say():\"); }");
                    method.setBody("{System.out.println(\"new method def " + i + "\");}");
//                    cc.writeFile();
                    byte[] bytecode = cc.toBytecode();

                    ClassDefinition definition = new ClassDefinition(Class.forName("rewriting.JavassistRedefinitionBenchmark$Foo"), bytecode);
                    RedefineClassAgent.redefineClasses(definition);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    @Benchmark
    public void redefine10(MyState s){
        s.redefine(10);
    }

    @Benchmark
    public void redefine100(MyState s){
        s.redefine(100);
    }

    @Benchmark
    public void redefine1000(MyState s){
        s.redefine(1000);
    }

    @Benchmark
    public void redefine10000(MyState s){
        s.redefine(10000);
    }
}
