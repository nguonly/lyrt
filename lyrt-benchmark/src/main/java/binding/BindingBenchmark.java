package binding;

import binding.specimen.CoreObject;
import binding.specimen.IBehavior;
import binding.specimen.R1;
import com.sun.org.apache.regexp.internal.RE;
import es.uniovi.reflection.invokedynamic.ProxyFactory;
import es.uniovi.reflection.invokedynamic.interfaces.Callable;
import es.uniovi.reflection.invokedynamic.util.Bootstrap;
import es.uniovi.reflection.invokedynamic.util.Cache;
import es.uniovi.reflection.invokedynamic.util.MethodSignature;
import net.lyrt.Compartment;
import net.lyrt.MethodSig;
import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/25/17.
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BindingBenchmark {

    public static final Class<? extends IBehavior> BASE_CLASS = IBehavior.class;

    /**
     * The default reference value. By defining the default reference value as a string type instead of as an object
     * type, the field is inlined by the compiler, similar to the primitive values.
     */
    public static final String DEFAULT_REFERENCE_VALUE = null;

    /**
     * The base class to be subclassed in all benchmarks.
     */
    private Class<? extends IBehavior> baseClass = BASE_CLASS;

    private String defaultReferenceValue = DEFAULT_REFERENCE_VALUE;

    private int urlLength = 0;

    private ClassLoader newClassLoader(){
        return new URLClassLoader(new URL[urlLength]);
    }

//    @Benchmark
//    public void test(){
//
//    }

    public IBehavior benchmarkJdkProxy(){
        return (IBehavior) Proxy.newProxyInstance(newClassLoader(),
            new Class<?>[]{baseClass},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Class<?> returnType = method.getReturnType();
                    CoreObject coreObject = new CoreObject();
                    return method.invoke(coreObject, args);
//                    if(returnType.isPrimitive()){
//                        return null;
//                    }else{
//                        return defaultReferenceValue;
//                    }
                }
            }
        );
    }

    public static void makeBinding(Compartment compartment, CoreObject[] cores, int amount){
        Registry reg = Registry.getRegistry();

//        cores = new CoreObject[amount];
//            System.out.println(cores.length);
        for(int i=0; i<amount; i++){
            cores[i] = reg.newCore(CoreObject.class);
        }

        try(InitBindingBlock ib = compartment.initBinding()){
            for(int i=0; i<amount; i++){
                cores[i].bind(R1.class);
            }
        }
    }

    @State(Scope.Thread)
    public static class MyState{
        public Compartment[] compartments;
        public CoreObject[][] coreObjects;

        Registry reg = Registry.getRegistry();

        @Setup
        public void setup(){
            int NUM = 4;

            compartments = new Compartment[NUM];
            coreObjects = new CoreObject[NUM][];

            for(int i=0; i<NUM; i++){
                compartments[i] = reg.newCompartment(Compartment.class);
                coreObjects[i] = new CoreObject[(int)Math.pow(10, i+1)];
            }
        }
    }

    @Benchmark
    public void binding10(MyState state){
        makeBinding(state.compartments[0], state.coreObjects[0], 10);
    }

    @Benchmark
    public void binding100(MyState state){
        makeBinding(state.compartments[1], state.coreObjects[1], 100);
    }

    @Benchmark
    public void binding1000(MyState state){
        makeBinding(state.compartments[2], state.coreObjects[2], 1000);
    }

    @Benchmark
    public void binding10000(MyState state){
        makeBinding(state.compartments[3], state.coreObjects[3], 10000);
    }
}
