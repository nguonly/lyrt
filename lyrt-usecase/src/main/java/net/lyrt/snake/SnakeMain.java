package net.lyrt.snake;

import net.lyrt.Compartment;
import net.lyrt.Registry;

/**
 * Created by nguonly on 6/25/17.
 */
public class SnakeMain {
    public static void main(String... args){
        System.out.println("Hello from Snake Game");

        Registry reg = Registry.getRegistry();
        Compartment comp = reg.newCompartment(Compartment.class);
    }
}
