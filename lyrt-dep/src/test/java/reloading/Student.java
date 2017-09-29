package reloading;

import net.lyrt.IRole;

public class Student implements IRole {
    public String getName(){
        System.out.println("Student role is modified...");
        return "Student";
    }
}
