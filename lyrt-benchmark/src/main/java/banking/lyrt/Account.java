package banking.lyrt;

import net.lyrt.Player;

/**
 * Created by nguonly on 6/29/17.
 */
public class Account extends Player{
    private int balance;

    public Account(){}

    public Account(int amount){
        balance = amount;
    }

    public void increase(int amount){
        balance += amount;
    }

    public void decrease(int amount){
        balance -= amount;
    }
}
