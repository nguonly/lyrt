package binding;

import binding.specimen.CoreObject;
import binding.specimen.IBehavior;
import binding.specimen.R1;
import binding.specimen.R2;
import es.uniovi.reflection.invokedynamic.ProxyFactory;
import es.uniovi.reflection.invokedynamic.interfaces.Callable;
import es.uniovi.reflection.invokedynamic.util.Bootstrap;
import es.uniovi.reflection.invokedynamic.util.Cache;
import es.uniovi.reflection.invokedynamic.util.MethodSignature;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import net.lyrt.Compartment;
import net.lyrt.MethodSig;
import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/26/17.
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MethodInvocationBenchmark {

    public static void main(String... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        MyState s = new MyState();

        CoreObject core = new CoreObject();
        Method method = CoreObject.class.getMethod("method", String.class);

        Object o = method.invoke(core, s.stringValue);
        System.out.println(o);
    }

    @State(Scope.Thread)
    public static class MyState {


        private String stringValue = "";
        private Object objectValue = new Object();

        private Compartment compartment;
        private CoreObject core;
        private CoreObject coreWithOneRole;
        private CoreObject coreWithTwoRoles;

        private IBehavior jdkProxyInstance;
        private IBehavior javassistInstance;
        private IBehavior bytebuddyInstance;
        private IBehavior cglibInstance;

        private Method reflectionMethod;

        HashMap<Integer, Callable<?>> hashCallables;
        private CoreObject indyCore;
        private Callable<?> callable;

        @Setup(Level.Invocation)
        public void setUp() throws InstantiationException {
            Registry reg = Registry.getRegistry();
            compartment = reg.newCompartment(Compartment.class);
            core = reg.newCore(CoreObject.class);
            coreWithOneRole = reg.newCore(CoreObject.class);
            coreWithTwoRoles = reg.newCore(CoreObject.class);

            try (InitBindingBlock ib = compartment.initBinding()) {
                coreWithOneRole.bind(R1.class);

                coreWithTwoRoles.bind(R1.class).bind(R2.class);
            }
            compartment.activate();

            //JDK Proxy
//            BindingBenchmark b = new BindingBenchmark();
//            jdkProxyInstance = b.benchmarkJdkProxy();
            jdkProxyInstance = createWithJdkProxy();
            javassistInstance = createWithJavassist();
            bytebuddyInstance = createWithByteBuddy();
            cglibInstance = createCglib();

            reflectionMethod = reflection();

            hashCallables = getHashCallables(CoreObject.class);
            indyCore = new CoreObject();
//        MethodSig ms = new MethodSig(String.class, "method", new Class<?>[]{String.class});
//        callable = hashCallables.get(ms.hashCode());
//        callable.invoke(indyCore, stringValue);

            callable = ProxyFactory.generateCallable(indyCore, "method", String.class, String.class);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            compartment.deactivate(false);
        }

        public HashMap<Integer, Callable<?>> getHashCallables(Class<?> objectClass) {
            HashMap<Integer, Callable<?>> hashCallable = new HashMap<>();

            Method[] methods = objectClass.getMethods();
            for (Method method : methods) {
                Callable<CoreObject> callable = getCallable(CoreObject.class, method);
                MethodSig ms = new MethodSig(method.getReturnType(), method.getName(), method.getParameterTypes());
                hashCallable.put(ms.hashCode(), callable);
            }
            return hashCallable;
        }

        private <T> Callable<T> getCallable(Class<T> clazz, Method method) {
            try {
                Bootstrap bootstrap = new Bootstrap(Cache.Save, "binding.indy.IndyBootstrap", "callDispatch", clazz, method.getName());
                MethodSignature sig = new MethodSignature(method.getReturnType(), clazz, method.getParameterTypes());

                //Class rType = method.getReturnType();
                Callable<T> callable = ProxyFactory.generateInvokeDynamicCallable(bootstrap, sig);

                return callable;
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        private IBehavior createWithJdkProxy(){
            final CoreObject core = new CoreObject();
            InvocationHandler invocationHandler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return method.invoke(core, args);
                }
            };
            return (IBehavior) Proxy.newProxyInstance(MethodInvocationBenchmark.class.getClassLoader(), new Class[]{IBehavior.class}, invocationHandler);
        }

        private IBehavior createWithJavassist() {
            try {
                javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
                factory.setSuperclass(CoreObject.class);
                Class aClass = factory.createClass();
                final IBehavior core;

                core = (IBehavior) aClass.newInstance();

                MethodHandler methodHandler = new MethodHandler() {
                    @Override
                    public Object invoke(Object self, Method overridden, Method proceed, Object[] args) throws Throwable {
                        return proceed.invoke(core, args);
                    }
                };

                ((ProxyObject) core).setHandler(methodHandler);
                return core;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        private IBehavior createWithByteBuddy(){
            Class<?> type = CoreObject.class;
            ClassLoader classLoader = type.getClassLoader();
            final CoreObject example = new CoreObject();
            Class<? extends IBehavior> proxyType = new ByteBuddy(ClassFileVersion.ofThisVm())
                    .subclass(CoreObject.class)
                    .method(ElementMatchers.isDeclaredBy(CoreObject.class))
                    .intercept(InvocationHandlerAdapter.of((proxy, method, args) -> method.invoke(example, args)))
                    .make()
                    .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded();

            try {
                return proxyType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        private IBehavior createCglib(){
            IBehavior core = (IBehavior) Enhancer.create(CoreObject.class, new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    return methodProxy.invokeSuper(o, objects);
                }
            });

            return core;
        }

        private Method reflection(){
            try {
                Method method = CoreObject.class.getMethod("method", String.class);
                return method;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Benchmark
    public void invocationWithoutRole(MyState s, Blackhole blackhole){
        blackhole.consume(s.core.invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void invocationWithOneRole(MyState s, Blackhole blackhole){
        blackhole.consume(s.coreWithOneRole.invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void invocationWithTwoRoles(MyState s, Blackhole blackhole){
        blackhole.consume(s.coreWithTwoRoles.invoke("method", String.class, s.stringValue));
    }

    @Benchmark
    public void jdkProxy(MyState s, Blackhole blackhole){
        blackhole.consume(s.jdkProxyInstance.method(s.stringValue));
    }

    @Benchmark
    public void javassistProxy(MyState s, Blackhole blackhole){
        blackhole.consume(s.javassistInstance.method(s.stringValue));
    }

    @Benchmark
    public void bytebuddyProxy(MyState s, Blackhole blackhole){
        blackhole.consume(s.bytebuddyInstance.method(s.stringValue));
    }

    @Benchmark
    public void cglibProxy(MyState s, Blackhole b){
        b.consume(s.cglibInstance.method(s.stringValue));
    }

    @Benchmark
    public void indyDirectInvocation(MyState s, Blackhole blackhole){
        blackhole.consume(s.callable.invoke(s.indyCore, s.stringValue));
    }

    @Benchmark
    public void indyLookupMethodInvocation(MyState s, Blackhole blackhole){
        MethodSig ms = new MethodSig(String.class, "method", new Class<?>[]{ String.class});
        s.callable = s.hashCallables.get(ms.hashCode());
        blackhole.consume(s.callable.invoke(s.indyCore, s.stringValue));
    }

    @Benchmark
    public void reflectionInvocation(MyState s, Blackhole b) throws InvocationTargetException, IllegalAccessException {
        b.consume(s.reflectionMethod.invoke(s.indyCore, s.stringValue));
    }

    @Benchmark
    public void staticInvocation(MyState s, Blackhole b){
        b.consume(s.indyCore.method(s.stringValue));
    }

    @Benchmark
    public void staticInvocation5(MyState s, Blackhole b){
        b.consume(s.indyCore.method(s.stringValue));
        b.consume(s.indyCore.method(s.stringValue));
        b.consume(s.indyCore.method(s.stringValue));
        b.consume(s.indyCore.method(s.stringValue));
        b.consume(s.indyCore.method(s.stringValue));
    }
}
