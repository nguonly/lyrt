package rewriting;

import binding.UnbindingBenchmark;
import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/1/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ByteBuddyRedefinitionBenchmark {

    public static void main(String... args){
        ByteBuddyAgent.install();

        redefine();
    }

    public static class Bar {
        public int i = 2;
        String m() { return "bar" + i; }
    }

    public static class Foo {
        public int i = 1;
        String m() { return "foo" + i; }
    }

    public static void redefine() {
        Foo foo = new Foo();
        Foo foo1 = new Foo();
        System.out.println("foo : " + foo.m());
        System.out.println("foo1: " + foo1.m());

        Runnable r = () -> {
            while(true) {
                System.out.println("foo1: " + foo1.m());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();

        new ByteBuddy().redefine(Bar.class)
                .name(Foo.class.getName())
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        System.out.println("foo : " + foo.m());
        System.out.println("foo1: " + foo1.m());
    }

    @State(Scope.Thread)
    public static class MyState{
        public Foo coreObject;

        @Setup(Level.Invocation)
        public void setup(){
            ByteBuddyAgent.install();

            coreObject = new Foo();

        }

        public void redefine(int num){
            for(int i=0; i<num; i++) {
                new ByteBuddy().redefine(Bar.class)
                        .name(Foo.class.getName())
                        .make()
                        .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
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
