package banking.lyrt;

import net.lyrt.Compartment;

/**
 * Created by nguonly on 6/29/17.
 */
public class Transaction extends Compartment{

    public void execute(Account source, Account target, int amount){
        source.invoke("withdraw", amount);
        target.invoke("deposit", amount);
    }
}
