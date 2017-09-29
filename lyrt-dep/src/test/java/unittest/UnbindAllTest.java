package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.helper.DumpHelper;
import org.junit.Test;

public class UnbindAllTest extends BaseTest{

    @Test
    public void testUnbindAllForSingleCore(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);

        comp.activate();
        lycog.bind(Student.class).bind(Father.class);

        DumpHelper.dumpRelations(comp);

        lycog.unbindAll();

        DumpHelper.dumpRelations(comp);
        DumpHelper.dumpLifting(comp);
        DumpHelper.dumpLowering(comp);
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
