package net.lyrt.tax;

import net.lyrt.IPlayer;
import net.lyrt.IRole;

public class Freelancer implements IRole{
    private double revenue;
    private double taxRate = 0.1; //10%

    public void earn(double amount){
        revenue += amount;
    }

    public double getMoney(){
        return revenue;
    }

    public double taxToBePaid(){
        double tax = revenue *taxRate;
        revenue -= tax;
        IPlayer player = (IPlayer) getCore();
        player.invoke("setSaving", revenue);

        return tax;
    }
}
