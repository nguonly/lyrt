package reloading;

import es.uniovi.reflection.invokedynamic.ProxyFactory;
import es.uniovi.reflection.invokedynamic.codegen.InvokedynamicClassLoader;
import es.uniovi.reflection.invokedynamic.interfaces.Callable;
import net.lyrt.*;
import net.lyrt.unanticipation.ClassReloader;
import org.junit.Assert;
import org.junit.Test;
import unittest.BaseTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionHelperTest extends BaseTest {

    @Test
    public void testUnanticipatedRoleInitialization() throws InterruptedException {
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);
        _reg.isUnanticipated = true;
//        lycog.setName("lycog");
        comp.activate();
        lycog.invoke("setName", "lycog");

        int counter = 0;
        while(counter<50) {
            lycog.bind(Student.class);
//            _reg.bind(RelationEnum.PLAY, comp.hashCode(), lycog.hashCode(), Student.class.getName());
//            _reg.bindTest(RelationEnum.PLAY, comp.hashCode(), lycog.hashCode(), Student.class.getName());

            String name = lycog.invoke("getName", String.class);
            System.out.println(name);

            Assert.assertEquals("Student", name);

            counter++;
            Thread.sleep(500);
        }
    }

    @Test
    public void testUnanticipatedDeepRoleInitialization() throws InterruptedException {
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);
        _reg.isUnanticipated = true;
//        lycog.setName("lycog");
        comp.activate();
        lycog.invoke("setName", "lycog");

        int counter = 0;
        while(counter<100) {
            lycog.bind(Student.class).bind(Father.class);
//            _reg.bind(RelationEnum.PLAY, comp.hashCode(), lycog.hashCode(), Student.class.getName());
//            _reg.bindTest(RelationEnum.PLAY, comp.hashCode(), lycog.hashCode(), Student.class.getName());

            String name = lycog.invoke("getName", String.class);
            System.out.println(name);

            Assert.assertEquals("Father", name);

            counter++;
            Thread.sleep(500);
        }
    }

    @Test
    public void testJindy() throws InstantiationException, IllegalAccessException, InterruptedException, ClassNotFoundException {
//        CoreObject indyCore = new CoreObject();
//        Callable<?> callable = ProxyFactory.generateCallable(indyCore, "method", String.class, String.class);
//        String msg = (String) callable.invoke(indyCore, "hello");
//        System.out.println(msg);

        int counter = 0;
//        while(counter<30){
//            Thread.sleep(500);
//            counter++;
//        }
        counter=0;
        while(counter<50) {
            ProxyFactory.clearAllCached();

//            Class<?> cls = new ClassReloader().loadClass("reloading.CoreObject");
//            InvokedynamicClassLoader icl = new InvokedynamicClassLoader();
//            Class<?> cls = icl.loadClass("reloading.CoreObject");
            Class<?> cls = InvokedynamicClassLoader.getClassLoader().loadClass("reloading.specimen.CoreObject");
            Object o = cls.newInstance();
            Callable<?> co = ProxyFactory.generateCallable(o, "method", String.class, String.class);

            String msg1 = (String) co.invoke(o, "hello");
            System.out.println(msg1);

            counter++;

            Thread.sleep(500);
        }
    }

    @Test
    public void testClassReloader() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, InterruptedException {
        int counter = 0;
        while(counter<50) {
            Class<?> cls = new ClassReloader().loadClass(CoreObject.class.getName());
            Object o = cls.newInstance();

            Method method = o.getClass().getMethod("method", String.class);

            String m = (String) method.invoke(o, "hello");
            System.out.println(m);

            counter++;
            Thread.sleep(500);
        }
    }

//    private Callable<?> getCallable(Class<?> clazz, Method method) {
//        try {
//            Bootstrap bootstrap = new Bootstrap(Cache.Save, "net.lyrt.IndyBootstrap", "callDispatch", clazz, method.getName());
//            MethodSignature sig = new MethodSignature(method.getReturnType(), clazz, method.getParameterTypes());
//
//            //Class rType = method.getReturnType();
//            Callable<?> callable = ProxyFactory.generateInvokeDynamicCallable(bootstrap, sig);
//
//            return callable;
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//
//        return null;
//    }


    public static class Person implements IPlayer {
        private String name;

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

    }

}
