package javassist;

/**
 * Created by nguonly on 7/2/17.
 */
public class HelloWorld extends Inject1 {
    public void sayHello(String name) {
        System.out.println("Hello "+name);

    }
    public HelloWorld() {
        super();
    }
    public HelloWorld(String targetClass,String targetMethod) {
        super(targetClass,targetMethod);
    }
}
