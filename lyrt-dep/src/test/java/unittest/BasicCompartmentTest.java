package unittest;

import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.block.InitBindingBlock;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/14/17.
 */
public class BasicCompartmentTest extends BaseTest {

    @Test
    public void testCompartmentInitialization(){
        Faculty inf = _reg.newCompartment(Faculty.class);

        Assert.assertEquals("Default Faculty", inf.getName());

        Faculty fe = _reg.newCompartment(Faculty.class, "FE");
        Assert.assertEquals("FE", fe.getName());

        Faculty fs = _reg.newCompartment(Faculty.class, "FS", 1);
        Assert.assertEquals("FS", fs.getName());
    }

    @Test
    public void testInitBindingBlock(){
        Faculty inf = _reg.newCompartment(Faculty.class);
        Person lycog = _reg.newCore(Person.class, "Lycog");

        lycog.invoke("setId", "L01");
        Assert.assertEquals("L01", lycog.invoke("getId", String.class));

        try(InitBindingBlock ib = inf.initBinding()){
            lycog.bind(Student.class);
        }

        inf.activate();
        lycog.invoke("setId", "S01"); //SetId of Student
        Assert.assertEquals("S01", lycog.invoke("getId", String.class));
        inf.deactivate();

        Assert.assertEquals("L01", lycog.invoke("getId", String.class));
    }

    public static class Faculty extends Compartment {
        private String _name;

        public Faculty(){
            _name = "Default Faculty";
        }

        public Faculty(String name){
            _name = name;
        }

        public Faculty(String name, Integer id){
            _name = name;
        }

        public String getName(){
            return _name;
        }
    }

    public static class Person extends Player {
        private String _name;

        public Person(){

        }

        public Person(String name){
            _name = name;
        }

        public void setName(String name){
            _name = name;
        }

        public String getName(){
            return _name;
        }

        private String _id;

        public void setId(String id){
            _id = id;
        }

        public String getId(){
            return _id;
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

        public String takeCourse(String course){
            return _id + " takes " + course;
        }
    }

    public static class LanguageStudent extends Role {
        private String _id;

        public void setId(String id){
            _id = id;
        }

        public String getId(){
            return _id;
        }
    }
}
