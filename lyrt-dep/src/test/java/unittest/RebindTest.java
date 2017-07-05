package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/25/17.
 */
public class RebindTest extends BaseTest{

    @Test
    public void testRebindFromCore(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class, "lycog");

        try(InitBindingBlock ib = comp.initBinding()){
            lycog.bind(Student.class);
        }

        comp.activate();

        Assert.assertEquals("00", lycog.invoke("getId", String.class));

        lycog.invoke("setId", "S01");

        Assert.assertEquals("S01", lycog.invoke("getId", String.class));

        DumpHelper.dumpRelations(comp);
        lycog.rebind(Student.class);
        Assert.assertEquals("00", lycog.invoke("getId", String.class));

        DumpHelper.dumpRelations(comp);
    }

    @Test
    public void testRebindFromRole(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);

        Role emp;
        try(InitBindingBlock ib = comp.initBinding()){
            emp = lycog.bind(Employee.class);
            emp.bind(Student.class);
        }

        comp.activate();

        DumpHelper.dumpRelations(comp);
        Assert.assertEquals("00", lycog.invoke("getId", String.class));

        lycog.invoke("setId", "S01");

        Assert.assertEquals("S01", lycog.invoke("getId", String.class));


        emp.rebind(Student.class);
        Assert.assertEquals("00", lycog.invoke("getId", String.class));

        DumpHelper.dumpRelations(comp);
    }

    public static class Person extends Player {
        private String _name;

        public Person(){}

        public Person(String name){
            _name = name;
        }

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

    public static class Student extends Role {
        private String _id = "00";

        public void setId(String id){
            _id = id;
        }

        public String getId(){
            return _id;
        }

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
}