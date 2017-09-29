package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import net.lyrt.helper.DumpHelper;
import net.lyrt.unanticipation.UnanticipatedXMLParser;
import net.lyrt.unanticipation.XMLConstructor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/23/17.
 */
public class UnanticipationTest extends BaseTest {

    @Test
    public void testBinding(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);

        comp.activate();

        Assert.assertEquals("Person", lycog.invoke("speak", String.class));

        String strXML = adaptionXML(comp.hashCode(), lycog.hashCode(), Student.class.getTypeName());

        System.out.println(strXML);

        //mock the runtime
        _reg.isUnanticipated = true;

        UnanticipatedXMLParser.parse(strXML);

        Assert.assertEquals("Student", lycog.invoke("speak", String.class));

        String unbindingXML = XMLConstructor.getXMLUnbindBaseOperation(comp.hashCode(), lycog.hashCode(), true, Student.class.getTypeName());

        System.out.println(unbindingXML);

        DumpHelper.dumpRelations(comp);
        UnanticipatedXMLParser.parse(unbindingXML);

        DumpHelper.dumpRelations(comp  );
        Assert.assertEquals("Person", lycog.invoke("speak", String.class));
    }

    @Test
    public void testUnbinding(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class);

        try(InitBindingBlock ib = comp.initBinding()){
            lycog.bind(Student.class);
        }

        comp.activate();
        Assert.assertEquals("Student", lycog.invoke("speak", String.class));

        String unbindXML = XMLConstructor.getXMLUnbindBaseOperation(comp.hashCode(), lycog.hashCode(), true, Student.class.getTypeName());

        UnanticipatedXMLParser.parse(unbindXML);

        Assert.assertEquals("Person", lycog.invoke("speak", String.class));
    }

    @Test
    public void testTransfer(){
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
        String transferXML = XMLConstructor.getXMLTransferOperation(comp.hashCode(), lycog.hashCode(), ely.hashCode(), Student.class.getTypeName());

        System.out.println(transferXML);

        UnanticipatedXMLParser.parse(transferXML);

        DumpHelper.dumpRelations(comp);
        Assert.assertEquals("S01", ely.invoke("getId", String.class));
    }

    @Test
    public void testRebind(){
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person lycog = _reg.newCore(Person.class, "lycog");

        //mock the runtime
        _reg.isUnanticipated = true;

        try(InitBindingBlock ib = comp.initBinding()){
            lycog.bind(Student.class);
        }

        comp.activate();

        Assert.assertEquals("00", lycog.invoke("getId", String.class));

        lycog.invoke("setId", "S01");

        Assert.assertEquals("S01", lycog.invoke("getId", String.class));

        String rebindXML = XMLConstructor.getXMLBindingBaseOperation(comp.hashCode(), lycog.hashCode(), true, false, Student.class.getTypeName());

        System.out.println(rebindXML);

        UnanticipatedXMLParser.parse(rebindXML);

        //Student role is re-initialized. Therefore, its state is lost.
        Assert.assertEquals("00", lycog.invoke("getId", String.class));
    }

    private String adaptionXML(int compartmentId, int playerId, String roleType){
        return XMLConstructor.getXMLBindingBaseOperation(compartmentId, playerId, true, true, roleType);
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
