package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/24/17.
 */
public class TransferTest extends BaseTest {

    @Test
    public void testTransferRoleFromCoreToCore(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class, "lycog");
        Person ely = _reg.newCore(Person.class, "ely");

        try(InitBindingBlock ib = comp.initBinding()){
            lycog.bind(Student.class);
        }

        comp.activate();

        lycog.invoke("setId", "S01");

        Assert.assertEquals("S01", lycog.invoke("getId", String.class));

        DumpHelper.dumpRelations(comp);
        lycog.transfer(Student.class, ely);

        DumpHelper.dumpRelations(comp);

        Assert.assertEquals("S01", ely.invoke("getId", String.class));
        Assert.assertEquals("Person", lycog.invoke("speak", String.class));
    }

    @Test
    public void testTransferDeepRoleFromCoreToCore(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class, "lycog");
        Person ely = _reg.newCore(Person.class, "ely");

        try(InitBindingBlock ib = comp.initBinding()){
            lycog.bind(Student.class).bind(Employee.class);
        }

        comp.activate();

        lycog.invoke("setId", "S001");

        Assert.assertEquals("S001", lycog.invoke("getId", String.class));
        Assert.assertEquals("Employee", lycog.invoke("speak", String.class));

        //Transfer Student and Employee to ely
        lycog.transfer(Student.class, ely);

        Assert.assertEquals("S001", ely.invoke("getId", String.class));
        Assert.assertEquals("Employee", ely.invoke("speak", String.class));
        Assert.assertEquals("Person", lycog.invoke("speak", String.class));
    }

    @Test
    public void testTransferTheLastRoleFromCoreToCore(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class, "lycog");
        Person ely = _reg.newCore(Person.class, "ely");

        try(InitBindingBlock ib = comp.initBinding()){
            ely.bind(Employee.class).bind(Student.class);
        }

        comp.activate();

        ely.invoke("setId", "A01");

        ely.transfer(Student.class, lycog);

        Assert.assertEquals("A01", lycog.invoke("getId", String.class));
        Assert.assertEquals("Student", lycog.invoke("speak", String.class));

        Assert.assertEquals("Employee", ely.invoke("speak", String.class));
    }

    public static class Person extends Player {
        private String _name;

        public Person(){

        }

        public Person(String name){
            this._name = name;
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
        private String _id;

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
