package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Relation;
import net.lyrt.Role;
import net.lyrt.block.AdaptationBlock;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.rollback.ControlUnit;
import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.TIMEOUT;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by nguonly on 6/22/17.
 */
public class CheckpointAsCompartmentDeactivationTest extends BaseTest {

    @Test
    public void applyCheckpointInActiveCompartmentTest() throws Throwable {
        Person p = _reg.newCore(Person.class);
        Compartment comp = _reg.newCompartment(Compartment.class);
        Compartment comp2 = _reg.newCompartment(Compartment.class);
        ControlUnit controlUnit = new ControlUnit();

        try(InitBindingBlock ib = comp.initBinding()) {
            p.bind(A.class).bind(C.class);
        }

        try(InitBindingBlock ib = comp2.initBinding()){
            p.bind(B.class).bind(D.class);
        }

        comp.activate();
        Assert.assertEquals("C", p.invoke("doThing", String.class));
        comp.deactivate();

        comp2.activate();
        Assert.assertEquals("D", p.invoke("doThing", String.class));

        //rolls back to comp NOT comp2
        controlUnit.rollback();

        //This is in comp2 activation scope
        Assert.assertEquals("C", p.invoke("doThing", String.class));

//        Stack<ArrayDeque<Relation>> stackVersion = _reg.getRollbackStacks().get(comp.hashCode());
//        Assert.assertEquals(1, stackVersion.size());
//
//        p.bind(B.class).bind(C.class);
//
//        ArrayDeque<Relation> v2Relations = stackVersion.pop();
//        List<Relation> v2 = v2Relations.stream()
//                .filter(c->c.compartment.equals(comp))
//                .collect(Collectors.toList());
//
//        v2.forEach(r -> {
//            String name = r.role.getClass().getSimpleName();
////            System.out.println(name);
//            Assert.assertTrue(name.contains("Person") || name.contains("A") || name.contains("C"));
//        });
    }

    @Test
    public void testParallelCheckpointAndRollback() throws InterruptedException {
        Person lycog = _reg.newCore(Person.class);
        Person ely = _reg.newCore(Person.class);
        Compartment c1 = _reg.newCompartment(Compartment.class);
        Compartment c21 = _reg.newCompartment(Compartment.class);
        Compartment c22 = _reg.newCompartment(Compartment.class);
        ControlUnit controlUnit = new ControlUnit();

        lycog.invoke("setName", "lycog");
        ely.invoke("setName", "ely");

        try(InitBindingBlock ib = c1.initBinding()){
            lycog.bind(A.class).bind(C.class);
        }

        Role a;
        try(InitBindingBlock ib = c21.initBinding()){
            a = ely.bind(A.class);
        }

        try(InitBindingBlock ib = c22.initBinding()){
            ely.bind(C.class).bind(B.class);
        }

        Runnable r1 = () -> {
            for(int i=0; i<10; i++) {
                c1.activate();

                Assert.assertEquals("C", lycog.invoke("doThing", String.class));

                try(AdaptationBlock ib = new AdaptationBlock()){
                    lycog.unbind(C.class);
                }

                Assert.assertEquals("A", lycog.invoke("doThing", String.class));

                controlUnit.rollback();

                Assert.assertEquals("C", lycog.invoke("doThing", String.class));

                c1.deactivate(false);

                System.out.println("i = " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable r2 = () -> {
            for(int j=0; j<10; j++) {
                c21.activate();
                Assert.assertEquals("A", ely.invoke("doThing", String.class));
                c21.deactivate();

                c22.activate();
                Assert.assertEquals("B", ely.invoke("doThing", String.class));

                controlUnit.rollback();

                Assert.assertEquals("A", ely.invoke("doThing", String.class));

                System.out.println("j = " + j);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        //pool.schedule(r1, 550, TimeUnit.MILLISECONDS);
        pool.execute(r1);
        pool.execute(r2);

        pool.awaitTermination(100*12, TimeUnit.MILLISECONDS);
        pool.shutdown();
    }

    public static class Person extends Player {
        private String name;

        public void setName(String name){
            this.name = name;
        }
        public String getName(){return name;}

        public String doThing(){
            return this.getClass().getSimpleName();
        }
    }

    public static class A extends Role {
        public String doThing(){
            return this.getClass().getSimpleName();
        }
    }

    public static class B extends Role{
        public String doThing(){
            return this.getClass().getSimpleName();
        }
    }

    public static class C extends Role{
        public String doThing(){
            return this.getClass().getSimpleName();
        }
    }

    public static class D extends Role{
        public String doThing(){
            return this.getClass().getSimpleName();
        }
    }
}
