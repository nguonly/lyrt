package reloading;

import net.lyrt.IRole;

public class Father implements IRole {
    public String getName(){
        System.out.println("Father role is modified...");
        return "Father";
    }
}
