package memory;

import binding.specimen.CoreObject;
import es.uniovi.reflection.invokedynamic.ProxyFactory;
import es.uniovi.reflection.invokedynamic.interfaces.Callable;
import es.uniovi.reflection.invokedynamic.util.Bootstrap;
import es.uniovi.reflection.invokedynamic.util.Cache;
import es.uniovi.reflection.invokedynamic.util.MethodSignature;
import net.lyrt.CallableMethod;
import net.lyrt.MethodSig;
import net.lyrt.Registry;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.HashMap;

public class JindyMemory {
    public static void main(String... args){
        JindyMemory mem = new JindyMemory();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        mem.make(10000);

        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.println("Memory Before : " + memoryBefore);
        System.out.println("Memory After  : " + ( memoryAfter));
        System.out.println("Overhead      : " + (memoryAfter - memoryBefore));
    }

    Registry reg = Registry.getRegistry();

    private void make(int num){
        CoreObject[] cores = new CoreObject[num];

        Class<?> clazz = CoreObject.class;
        for(int i=0; i<cores.length; i++){
            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                Callable<?> c = getCallable(clazz, method);
            }
        }
    }

    private Callable<?> getCallable(Class<?> clazz, Method method) {
        try {
            Bootstrap bootstrap = new Bootstrap(Cache.Save, "net.lyrt.IndyBootstrap", "callDispatch", clazz, method.getName());
            MethodSignature sig = new MethodSignature(method.getReturnType(), clazz, method.getParameterTypes());

            //Class rType = method.getReturnType();
            Callable<?> callable = ProxyFactory.generateInvokeDynamicCallable(bootstrap, sig);

            return callable;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
