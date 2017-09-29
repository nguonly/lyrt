package net.lyrt.filetransfer.server;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.filetransfer.server.role.AES;
import net.lyrt.unanticipation.ClassReloader;

import java.io.File;

public class TestMain {
    public static void main(String... args) throws InterruptedException {

        Registry reg = Registry.getRegistry();
        Channel ch = reg.newCore(Channel.class);
        Compartment compartment = reg.newCompartment(Compartment.class);
        ClassReloader.classPath = "lyrt-usecase" + File.separator + "target" + File.separator + "classes";
        System.out.println(ClassReloader.classPath);
        System.out.println(System.getProperty("user.dir"));

        reg.isUnanticipated=true;
        compartment.activate();

        int counter = 0;

        while(counter<100){
            ch.bind(AES.class);
            String msg = ch.invoke("format", String.class, "hello");

            System.out.println(msg);

            counter++;
            Thread.sleep(500);
        }
    }
}
