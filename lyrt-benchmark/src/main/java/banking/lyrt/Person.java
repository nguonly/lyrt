package banking.lyrt;

import net.lyrt.Player;

/**
 * Created by nguonly on 6/29/17.
 */
public class Person extends Player {
    private String name;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
