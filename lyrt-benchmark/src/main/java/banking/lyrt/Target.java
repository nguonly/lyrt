package banking.lyrt;

import net.lyrt.Role;

/**
 * Created by nguonly on 6/29/17.
 */
public class Target extends Role {
    public void deposit(int amount){
        invokeCore("increase", amount);
    }
}
