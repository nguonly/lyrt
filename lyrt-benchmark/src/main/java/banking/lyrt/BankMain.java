package banking.lyrt;

import net.lyrt.Registry;
import net.lyrt.block.InitBindingBlock;

/**
 * Created by nguonly on 6/29/17.
 */
public class BankMain {
    public static void main(String... args){
        Registry reg = Registry.getRegistry();

        Account p1 = reg.newCore(Account.class);
        Account p2 = reg.newCore(Account.class);
        Transaction t = reg.newCompartment(Transaction.class);

        try(InitBindingBlock ib = t.initBinding()){
            p1.bind(Source.class);
            p2.bind(Target.class);
        }

        t.activate();
        t.execute(p1, p2, 20);
        t.deactivate();
    }
}
