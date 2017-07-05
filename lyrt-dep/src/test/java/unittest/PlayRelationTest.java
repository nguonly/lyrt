package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/14/17.
 */
public class PlayRelationTest extends BaseTest {

    @Test
    public void testBehaviorDependent() throws Throwable{

        Person p = _reg.newCore(Person.class);

        Assert.assertEquals("Person", p.speak());

        MyCompartment comp = _reg.newCompartment(MyCompartment.class);
        comp.activate();

        DumpHelper.dumpLifting(comp);
        p.bind(Student.class);
        DumpHelper.dumpRelations(comp);
        DumpHelper.dumpLifting(comp);
        Assert.assertEquals("Student", p.invoke("speak", String.class));


        Person p2 = _reg.newCore(Person.class);
        p2.bind(Father.class);
        Assert.assertEquals("Father", p2.invoke("speak", String.class));

    }

    @Test
    public void testCallMethodOfCore() throws Throwable{

        Person p = _reg.newCore(Person.class);

        MyCompartment comp = _reg.newCompartment(MyCompartment.class);
        comp.activate();
        p.setName("lycog");

        p.bind(Student.class);

//        _reg.displayCallables(_reg.callables);
        p.setName("lycog");

        DumpHelper.dumpRelations(comp);

        Assert.assertEquals("lycog", p.invoke("getName", String.class));
    }

    @Test
    public void testCallCoreMethodWithoutBindingInActiveCompartment(){
        MyCompartment comp = _reg.newCompartment(MyCompartment.class);
        Person p = _reg.newCore(Person.class);

        Assert.assertEquals("Person", p.invoke("speak", String.class));

        DumpHelper.dumpCoreCallable(p);

        //without binding
        comp.activate();
        Assert.assertEquals("Person", p.invoke("speak", String.class));
        comp.deactivate();
    }

    @Test
    public void testUnbindingFromACore(){
        MyCompartment comp = _reg.newCompartment(MyCompartment.class);
        Person p = _reg.newCore(Person.class);

        p.invoke("setName", "lycog");

        try(InitBindingBlock ib = comp.initBinding()){
            p.bind(Student.class);
        }

        Assert.assertEquals("Person", p.invoke("speak", String.class));

        comp.activate();
        Assert.assertEquals("Student", p.invoke("speak", String.class));

        DumpHelper.dumpLifting(comp);
        p.unbind(Student.class);
        DumpHelper.dumpLifting(comp);

        Assert.assertEquals("Person", p.invoke("speak", String.class));
        comp.deactivate();
    }

    @Test
    public void testUnbindingForDeepRelationFromACore(){
        MyCompartment comp = _reg.newCompartment(MyCompartment.class);
        Person p = _reg.newCore(Person.class);

        try(InitBindingBlock ib = comp.initBinding()){
            p.bind(Student.class).bind(Father.class).bind(Employee.class);
        }

        DumpHelper.dumpRelations(comp);

        comp.activate();
        Assert.assertEquals("Employee", p.invoke("speak", String.class));
        p.unbind(Student.class);
        DumpHelper.dumpRelations(comp);
        Assert.assertEquals("Person", p.invoke("speak", String.class));
        comp.deactivate();
    }

    @Test
    public void testUnbindingForDeepRelationFromARole(){
        MyCompartment comp = _reg.newCompartment(MyCompartment.class);
        Person p = _reg.newCore(Person.class);

        try(InitBindingBlock ib = comp.initBinding()){
            p.bind(Student.class).bind(Employee.class).bind(Father.class);
        }

        comp.activate();
        Assert.assertEquals("Father", p.invoke("speak", String.class));
        DumpHelper.dumpRelations(comp);
        p.unbind(Employee.class);
        DumpHelper.dumpRelations(comp);
        Assert.assertEquals("Student", p.invoke("speak", String.class));
    }

    @Test
    public void testUnbindingTheLastRoleInDeepRelation(){
        MyCompartment comp = _reg.newCompartment(MyCompartment.class);
        Person p = _reg.newCore(Person.class);

        try(InitBindingBlock ib = comp.initBinding()){
            p.bind(Student.class).bind(Employee.class).bind(Father.class);
        }

        comp.activate();
        Assert.assertEquals("Father", p.invoke("speak", String.class));
        DumpHelper.dumpRelations(comp);
        p.unbind(Father.class);
        DumpHelper.dumpRelations(comp);
        Assert.assertEquals("Employee", p.invoke("speak", String.class));
    }

    public static class Person extends Player {
        private String _name;

        public void setName(String name){
            _name = name;
        }

        public String getName(){
            return _name;
        }

        public String speak(){
            return "Person";
        }
    }

    public static class Student extends Role{
        public String speak(){
            return "Student";
        }

    }

    public static class Father extends Role {
        public String speak(){
            return "Father";
        }
    }

    public static class Employee extends Role{
        public String speak() { return "Employee"; }
    }

    public static class MyCompartment extends Compartment {

    }
}
