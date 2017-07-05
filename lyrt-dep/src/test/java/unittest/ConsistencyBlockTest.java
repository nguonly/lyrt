package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.ConsistencyBlock;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 6/20/17.
 */
public class ConsistencyBlockTest extends BaseTest {

    @Test
    public void testBasicConsistencyBlock() throws InterruptedException {
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
        try(ConsistencyBlock cb = new ConsistencyBlock()) {
            for (int i = 0; i <= 5; i++) {
                String pName = p.invoke("getName", String.class);
                Assert.assertEquals("lycog", pName);
                Thread.sleep(100);
            }

            //this block of code should NOT be affected because of adaptation
            for (int i = 0; i < 5; i++) {
                String pName = p.invoke("getName", String.class);
//                System.out.println(pName);
                Assert.assertEquals("lycog", pName);
                Thread.sleep(100);
            }
        }
        comp.deactivate();
    }

    @Test
    public void testAdaptationAfterConsistencyBlock() throws InterruptedException {
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person p = _reg.newCore(Person.class);

        p.invoke("setName", "lycog");

        Runnable r1 = () -> {
            comp.activate();
            p.bind(Father.class);
            comp.deactivate();
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(r1, 550, TimeUnit.MILLISECONDS);

        Assert.assertEquals("lycog", p.invoke("getName", String.class));

        comp.activate();
        try(ConsistencyBlock cb = new ConsistencyBlock()) {
            for (int i = 0; i <= 5; i++) {
                String pName = p.invoke("getName", String.class);
                Assert.assertEquals("lycog", pName);
                Thread.sleep(100);
            }

            //Adaptation takes place
            //this block of code should NOT be affected because of adaptation
            for (int i = 0; i < 5; i++) {
                String pName = p.invoke("getName", String.class);
//                System.out.println(pName);
                Assert.assertEquals("lycog", pName);
                Thread.sleep(100);
            }
        }

        //After consistency block
        for(int i=0; i<5; i++){
            String pName = p.invoke("getName", String.class);
            Assert.assertEquals("Father", pName);
        }
        comp.deactivate();
    }

    @Test
    public void testAdaptationAsUnbindingARole(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person p = _reg.newCore(Person.class);

        p.invoke("setName", "lycog");

        try(InitBindingBlock ib = comp.initBinding()){
            p.bind(Father.class);
        }

        Runnable r1 = () -> {
            comp.activate();
            p.unbind(Father.class);
            comp.deactivate();
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(r1, 550, TimeUnit.MILLISECONDS);

        comp.activate();

        try(ConsistencyBlock cb1 = new ConsistencyBlock()) {
            for (int i = 0; i <= 5; i++) {
                String pName = p.invoke("getName", String.class);
                Assert.assertEquals("Father", pName);
                Thread.sleep(100);
            }

            //Adaptation takes place
            //this block of code should NOT be affected because of adaptation
            for (int i = 0; i < 5; i++) {
                String pName = p.invoke("getName", String.class);
//                System.out.println(pName);
                Assert.assertEquals("Father", pName);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DumpHelper.dumpRelations(comp);

        String pName = p.invoke("getName", String.class);
        Assert.assertEquals("lycog", pName);

    }

    @Test
    public void testUnbindingFromRoleInDeepRelation(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person p = _reg.newCore(Person.class);

        p.invoke("setName", "lycog");

        try(InitBindingBlock ib = comp.initBinding()){
            p.bind(Father.class).bind(Student.class).bind(Employee.class);
        }

        Runnable r1 = () -> {
            comp.activate();
            p.unbind(Student.class);
            comp.deactivate();
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(r1, 550, TimeUnit.MILLISECONDS);

        comp.activate();

        try(ConsistencyBlock cb1 = new ConsistencyBlock()) {
            for (int i = 0; i <= 5; i++) {
                String pName = p.invoke("getName", String.class);
                Assert.assertEquals("Employee", pName);
                Thread.sleep(100);
            }

            //Adaptation takes place
            //this block of code should NOT be affected because of adaptation
            for (int i = 0; i < 5; i++) {
                String pName = p.invoke("getName", String.class);
//                System.out.println(pName);
                Assert.assertEquals("Employee", pName);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DumpHelper.dumpRelations(comp);

        String pName = p.invoke("getName", String.class);
        Assert.assertEquals("Father", pName);
    }

    @Test
    public void testUnbindingFromCoreInDeepRelation(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person p = _reg.newCore(Person.class);

        p.invoke("setName", "lycog");

        try(InitBindingBlock ib = comp.initBinding()){
            p.bind(Father.class).bind(Student.class).bind(Employee.class);
        }

        Runnable r1 = () -> {
            comp.activate();
            p.unbind(Father.class);
            comp.deactivate();
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(r1, 550, TimeUnit.MILLISECONDS);

        comp.activate();

        try(ConsistencyBlock cb1 = new ConsistencyBlock()) {
            for (int i = 0; i <= 5; i++) {
                String pName = p.invoke("getName", String.class);
                Assert.assertEquals("Employee", pName);
                Thread.sleep(100);
            }

            //Adaptation takes place
            //this block of code should NOT be affected because of adaptation
            for (int i = 0; i < 5; i++) {
                String pName = p.invoke("getName", String.class);
//                System.out.println(pName);
                Assert.assertEquals("Employee", pName);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DumpHelper.dumpRelations(comp);

        String pName = p.invoke("getName", String.class);
        Assert.assertEquals("lycog", pName);
    }

    @Test
    public void testUnbindingFromCoreForMultipleCores(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);
        Person ely = _reg.newCore(Person.class);

        lycog.invoke("setName", "lycog");
        ely.invoke("setName", "ely");

        try(InitBindingBlock ib = comp.initBinding()){
            lycog.bind(Father.class);
            ely.bind(Employee.class);
        }

        Runnable r1 = () -> {
            comp.activate();
            ely.unbind(Employee.class);
            comp.deactivate();
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(r1, 550, TimeUnit.MILLISECONDS);

        comp.activate();

        try(ConsistencyBlock cb1 = new ConsistencyBlock()) {
            for (int i = 0; i <= 5; i++) {
                String elyName = ely.invoke("getName", String.class);
                Assert.assertEquals("Employee", elyName);

                Assert.assertEquals("Father", lycog.invoke("getName", String.class));
                Thread.sleep(100);
            }

            //Adaptation takes place
            //this block of code should NOT be affected because of adaptation
            for (int i = 0; i < 5; i++) {
                String pName = ely.invoke("getName", String.class);
//                System.out.println(pName);
                Assert.assertEquals("Employee", pName);

                Assert.assertEquals("Father", lycog.invoke("getName", String.class));
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DumpHelper.dumpRelations(comp);

        String pName = ely.invoke("getName", String.class);
        Assert.assertEquals("ely", pName);

        Assert.assertEquals("Father", lycog.invoke("getName", String.class));
    }

    @Test
    public void testUnbindingFromRoleOfMultipleCores(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);
        Person ely = _reg.newCore(Person.class);

        lycog.invoke("setName", "lycog");
        ely.invoke("setName", "ely");

        try(InitBindingBlock ib = comp.initBinding()){
            lycog.bind(Father.class).bind(Employee.class);
            ely.bind(Employee.class).bind(Student.class);
        }

        Runnable r1 = () -> {
            comp.activate();
            ely.unbind(Student.class);
            lycog.unbind(Employee.class);
            comp.deactivate();
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(r1, 550, TimeUnit.MILLISECONDS);

        comp.activate();

        try(ConsistencyBlock cb1 = new ConsistencyBlock()) {
            for (int i = 0; i <= 5; i++) {
                String elyName = ely.invoke("getName", String.class);
                Assert.assertEquals("Student", elyName);

                Assert.assertEquals("Employee", lycog.invoke("getName", String.class));
                Thread.sleep(100);
            }

            //Adaptation takes place
            //this block of code should NOT be affected because of adaptation
            for (int i = 0; i < 5; i++) {
                String pName = ely.invoke("getName", String.class);
//                System.out.println(pName);
                Assert.assertEquals("Student", pName);

                Assert.assertEquals("Employee", lycog.invoke("getName", String.class));
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DumpHelper.dumpRelations(comp);

        String pName = ely.invoke("getName", String.class);
        Assert.assertEquals("Employee", pName);

        Assert.assertEquals("Father", lycog.invoke("getName", String.class));
    }

    public static class Person extends Player {
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

    public static class Employee extends Role{
        public String getName() {return "Employee";}
    }
}
