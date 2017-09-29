package net.lyrt.test;

import net.lyrt.Registry;
import net.lyrt.unanticipation.ClassReloader;

import java.io.File;

/**
 * Created by nguonly on 6/25/17.
 */
public class TestMain {
    public static void main(String... args){
        TestObject o1 = new TestObject();
        TestObject o2 = new TestObject();

        o1.setName("lycog");
        o2.setName("ely");

        o1.display();
        o2.display();

        Registry reg = Registry.getRegistry();
        reg.isUnanticipated = true;
        ClassReloader.classPath = "lyrt-usecase" + File.separator + "target" + File.separator + "classes";
        TestObject to = reg.newCore(TestObject.class);

        System.out.println(to);
    }
}
