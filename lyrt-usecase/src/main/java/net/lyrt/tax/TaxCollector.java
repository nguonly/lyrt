package net.lyrt.tax;

import net.lyrt.IPlayer;
import net.lyrt.IRole;

public class TaxCollector implements IRole{
    public void collectTax(double amount, String payer){
        Person collector = (Person)getCore();
//        String taxEmployeeName = taxEmployee.invoke("getName", String.class);

        invokeCompartment("setRevenue", void.class, amount);

        System.out.println(collector.getName() + " gets " + amount + " of tax from " + payer);
    }

    public void collectTax(){
        invokeRel(TaxPayer.class, "pay");
    }
}
