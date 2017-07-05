package dummy;

import net.lyrt.Player;
import net.lyrt.Role;
import net.lyrt.Registry;

/**
 * Created by nguonly on 5/12/17.
 */
public class BasicInvocationMain {
    public static void main(String... args){
        BasicInvocationMain mm = new BasicInvocationMain();
        mm.test();
    }

    public void test(){
        Registry reg = Registry.getRegistry();
        Person p = reg.newCore(Person.class);

        String msg = p.invoke("getName", String.class);
        System.out.println("Before binding: " + msg);

        p.bind(Student.class);

        msg = p.invoke("getName", String.class);
        System.out.println("After binding: " + msg);
    }

    public static class Person extends Player {
        public String getName(){
            return this.getClass().getSimpleName();
        }
    }

    public static class Student extends Role {
        public String getName(){
            return this.getClass().getSimpleName();
        }
    }
}
