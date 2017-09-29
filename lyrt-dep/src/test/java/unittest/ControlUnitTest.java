package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Relation;
import net.lyrt.Role;
import net.lyrt.block.AdaptationBlock;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import net.lyrt.rollback.ControlUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by nguonly on 6/22/17.
 */
public class ControlUnitTest extends BaseTest{

    @Test
    public void applyCheckpointInActiveCompartmentTest() throws Throwable {
        Person p = _reg.newCore(Person.class);
        Compartment comp = _reg.newCompartment(Compartment.class);
        ControlUnit controlUnit = new ControlUnit();

        try(InitBindingBlock ib = comp.initBinding()) {
            p.bind(A.class).bind(C.class);
        }

        comp.activate();
        controlUnit.checkpoint();

        long threadId = Thread.currentThread().getId();
        Stack<ArrayDeque<Relation>> stackVersion = _reg.getRollbackStacks().get(comp.hashCode());
        Assert.assertEquals(1, stackVersion.size());

        p.bind(B.class).bind(C.class);

        ArrayDeque<Relation> v2Relations = stackVersion.pop();
        List<Relation> v2 = v2Relations.stream()
                .filter(c->c.compartment.equals(comp))
                .collect(Collectors.toList());

        v2.forEach(r -> {
            String name = r.role.getClass().getSimpleName();
//            System.out.println(name);
            Assert.assertTrue(name.contains("Person") || name.contains("A") || name.contains("C"));
        });
    }

    @Test
    public void rollbackInCompartmentTest() throws Throwable {
        Person p = _reg.newCore(Person.class);
        Compartment comp = _reg.newCompartment(Compartment.class);
        ControlUnit controlUnit = new ControlUnit();

        comp.activate();
        p.bind(A.class).bind(B.class);

        controlUnit.checkpoint(comp);

        p.bind(C.class);
        p.bind(D.class);

        controlUnit.rollback();

//        DumpHelper.dumpRelations();
        ArrayDeque<Relation> v1Relations = comp.getRelations();
        List<Relation> v1 = v1Relations.stream()
                .filter(c->c.compartment.equals(comp) && c.depth>0)
                .collect(Collectors.toList());

        v1.forEach(r -> {
            String name = r.role.getClass().getSimpleName();
            Assert.assertTrue(name.contains("A") || name.contains("B"));
        });
    }

    @Test
    public void newCheckpointRemovingRoleTest() throws Throwable {
        Person p = _reg.newCore(Person.class);

        Compartment comp = _reg.newCompartment(Compartment.class);
        ControlUnit controlUnit = new ControlUnit();

        _reg.isUnanticipated=false;

        comp.activate();

        try(AdaptationBlock ac = new AdaptationBlock()) {
            p.bind(A.class);
            p.bind(B.class);
        }

        Assert.assertEquals("B", p.invoke("doThing", String.class));

        try(AdaptationBlock ac = new AdaptationBlock()) {
            p.unbind(B.class);
        }

        Assert.assertEquals("A", p.invoke("doThing", String.class));

        controlUnit.rollback();

        Assert.assertEquals("B", p.invoke("doThing", String.class));

        try(AdaptationBlock ac = new AdaptationBlock()) {
            p.bind(C.class);
        }

        Assert.assertEquals("C", p.invoke("doThing", String.class));

        controlUnit.rollback();

        Assert.assertEquals("B", p.invoke("doThing", String.class));

        comp.deactivate();

        Assert.assertEquals("Person", p.invoke("doThing", String.class));

    }

    @Test
    public void rollbackOnEmptyStack(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);

        comp.activate();

        try(AdaptationBlock ib = new AdaptationBlock()){
            lycog.bind(A.class).bind(B.class);
        }
        DumpHelper.dumpStacks(comp);
        DumpHelper.dumpRelations(comp);

        ControlUnit cu = new ControlUnit();

        cu.rollback(comp);

        DumpHelper.dumpRelations(comp);

        try(AdaptationBlock ib = new AdaptationBlock()){
            lycog.bind(A.class).bind(B.class);
        }

        DumpHelper.dumpRelations(comp);
        DumpHelper.dumpStacks(comp);

        cu.rollback(comp);

        DumpHelper.dumpRelations(comp);
        DumpHelper.dumpStacks(comp);
    }

    public static class Person extends Player {
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
