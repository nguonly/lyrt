package javassist;

import rewriting.RedefineClassAgent;

import java.lang.instrument.ClassDefinition;

/**
 * Created by nguonly on 7/2/17.
 */
public class Test1 {
    public static void main(String[] args) throws Exception {
        byte[] byteArray = null;
        HelloWorld inject1 = new HelloWorld("javassist.HelloWorld","sayHello");
        inject1.injectAround();
        byteArray =  inject1.injectBefore();

//        HotSwapper hs = new HotSwapper(8000);
//        hs.reload("javassist.HelloWorld", byteArray);
//        inject1.sayHello("Sandeep");

        ClassDefinition definition = new ClassDefinition(Class.forName("javassist.HelloWorld"), byteArray);
        RedefineClassAgent.redefineClasses(definition);
        inject1.sayHello("Sandeep");
    }
}
