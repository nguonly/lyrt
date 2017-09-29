package unittest;

import net.lyrt.Compartment;
import net.lyrt.IPlayer;
import net.lyrt.Player;
import net.lyrt.Role;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/20/17.
 */
public class AsynchronousBindingTest extends BaseTest {

    @Test
    public void testBasicAsyncBinding() throws InterruptedException {
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person p = _reg.newCore(Person.class);

        p.invoke("setName", "lycog");

        Runnable r1 = () -> {
            comp.activate();
            p.bind(Student.class);
            comp.deactivate();
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(r1, 550, TimeUnit.MILLISECONDS);

        Assert.assertEquals("lycog", p.invoke("getName", String.class));

        comp.activate();
        for(int i=0; i<=5; i++){
            String pName = p.invoke("getName", String.class);
            Assert.assertEquals("lycog", pName);
            Thread.sleep(100);
        }
        for(int i=0; i<5; i++){
            String pName = p.invoke("getName", String.class);
            Assert.assertEquals("Student", pName);
            Thread.sleep(100);
        }
        comp.deactivate();
    }

    public static class Person implements IPlayer {
        private String name;

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

    }

    public static class Student extends Role {
        public String getName(){
            return "Student";
        }
    }

    public static class Father extends Role{
        public String getName(){
            return "Father";
        }
    }
}
