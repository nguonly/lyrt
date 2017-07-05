package banking.lyrt;

import net.lyrt.Role;

/**
 * Created by nguonly on 6/29/17.
 */
public class Source extends Role {
    public void withdraw(int amount){
        invokeCore("decrease", amount);
    }
}
