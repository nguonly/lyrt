package unittest;

import net.lyrt.Player;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nguonly on 6/19/17.
 */
public class LiftingPrimitiveInvocationTest extends BaseTest {

    @Test
    public void testInvocationWithPrimitive(){
        Person p = _reg.newCore(Person.class);

        p.invoke("setName", "lycog");
        p.invoke("setAge",18);

        Assert.assertEquals("lycog", p.invoke("getName", String.class));

        int age = p.invoke("getAge", int.class);
        Assert.assertEquals(18, age);
    }

    public static class Person extends Player {
        private String name;

        private int age;

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public void setAge(int age){
            this.age = age;
        }

        public int getAge(){return age;}
    }
}
