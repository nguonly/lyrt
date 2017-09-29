package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.AdaptationBlock;
import net.lyrt.helper.DumpHelper;
import net.lyrt.rollback.ControlUnit;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/22/17.
 */
public class ControlUnitOnStateTest extends BaseTest {

    @Test
    public void statePreserveTest() throws Throwable {
        Compartment comp = _reg.newCompartment(Compartment.class);
        Person person = _reg.newCore(Person.class);
        person.setName("Lycog");
        ControlUnit controlUnit = new ControlUnit();

        comp.activate();

        Assert.assertEquals("Lycog", person.invoke("getName", String.class));

        Role a;
        try(AdaptationBlock ac = new AdaptationBlock()){
            a = (A)person.bind(A.class, "myA");
        }

        Assert.assertEquals("myA", person.invoke("getName", String.class));

        try(AdaptationBlock ac = new AdaptationBlock()){
            a.bind(B.class, "myB");
        }

        Assert.assertEquals("myB", person.invoke("getName", String.class));

        try(AdaptationBlock ac = new AdaptationBlock()){
            a.bind(C.class, "myC");
        }
        Assert.assertEquals("myC", person.invoke("getName", String.class));

        DumpHelper.dumpRelations(comp);

        controlUnit.rollback(); //remove role "C.class)

        DumpHelper.dumpRelations(comp);
        Assert.assertEquals("myB", person.invoke("getName", String.class));

        controlUnit.rollback();

        Assert.assertEquals("myA", person.invoke("getName", String.class));
    }

    public static class Person extends Player {
        private String name;

//        public Person(){
//
//        }
//
//        public Person(String name){
//            this.name = name;
//        }

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }

    public static class A extends Role {
        private String name = "AA";

        public A(){

        }

        public A(String name){
            this.name = name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }

    public static class B extends Role {
        private String name;

        public B(){

        }

        public B(String name){
            this.name = name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }

    public static class C extends Role {
        private String name;

        public C(){

        }

        public C(String name){
            this.name = name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }
}
