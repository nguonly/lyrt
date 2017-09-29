package net.lyrt.tax;

import net.lyrt.IPlayer;
import net.lyrt.IRole;

public class TaxPayer implements IRole {
    public void pay(){
        IPlayer payer = (IPlayer)getCore();
        double tax = payer.invoke("taxToBePaid", double.class);
        String payerName = payer.invoke("getName", String.class);

        invokeCompartment("setRevenue", tax);

        System.out.println(payerName + " pays a tax of " + tax + " Euro");

//        IPlayer player = (IPlayer)getCore();
//        player.invoke("collectTax", tax);
//        Object[] players = getCores(TaxCollector.class);
//        for(Object player: players){
//            ((IPlayer)player).invoke("collectTax", tax, payerName);
//        }
    }
}
