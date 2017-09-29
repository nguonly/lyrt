package memory;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import es.uniovi.reflection.invokedynamic.ProxyFactory;
import es.uniovi.reflection.invokedynamic.interfaces.Callable;
import es.uniovi.reflection.invokedynamic.util.Bootstrap;
import es.uniovi.reflection.invokedynamic.util.Cache;
import es.uniovi.reflection.invokedynamic.util.MethodSignature;
import net.lyrt.CallableMethod;
import net.lyrt.MethodSig;

import java.lang.reflect.Method;

public class JindyCallable implements IMemoryOverhead{
//    int num;
//
//    public JindyCallable(int num){
//        this.num = num;
//    }

    @Override
    public void setup() {

    }

    @Override
    public Object proceed(int numCore, int numRole) {
        CoreObject[] cores = new CoreObject[numCore];


//        int numBinding = numRole/numCore;
        for(int i=0; i<numCore; i++){
            cores[i] = new CoreObject();

            Method[] methods = CoreObject.class.getDeclaredMethods();
            for (Method method : methods) {
                    Callable<?> c = getCallable(CoreObject.class, method);
            }
//            for(int j=0; j<numBinding; j++){
//                Method[] methods = R1.class.getDeclaredMethods();
//
//                for (Method method : methods) {
//                    Callable<?> c = getCallable(R1.class, method);
//
////                    MethodSig ms = getMethodSig(method);
////                    String m = String.format("%d:%s", object.hashCode(), ms.toString());
////                    //CallableMethod cm = new CallableMethod(m, object, c);
////                    CallableMethod cm = new CallableMethod(m, object, c, method);
////                    hashCallable.put(m.hashCode(), cm);
//                }
//            }

        }
        return cores;
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

    public static void main(String... args) throws InterruptedException {
        Thread.sleep(5000);
        String[] str = new String[]{"-C5000", "-R5000"};
        new MemoryOverhead(args).run(new JindyCallable());
        Thread.sleep(2000);
    }
}