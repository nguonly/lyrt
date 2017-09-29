package net.lyrt.helper;

import net.lyrt.*;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by nguonly on 6/14/17.
 */
public class DumpHelper {


    public static void dumpRelations(List<Relation> relations){
        System.out.println("--------------- Dump Relations --------------");
        System.out.format("%30s %12s %12s %30s %30s %30s %5s\n",
                "Compartment", "BoundTime", "UnBoundTime", "Core", "Player", "Role", "Lvl");

        relations.forEach(System.out::println);

        System.out.println("---------------- End of Dump Relations -----");
    }

    public static void dumpRelations(ArrayDeque<Relation> relations){
        dumpRelations(relations.stream().collect(Collectors.toList()));
    }

    public static void dumpRelations(Compartment compartment){
        dumpRelations(compartment.getRelations());
    }

    public static void dumpLifting(Compartment compartment){
        ConcurrentHashMap<Integer, CallableMethod> liftingCallable = compartment.getLiftingCallable();
        System.out.println("---------- Dump Lifting Callable -----------");
        liftingCallable.forEach((k, v) -> System.out.println(String.format("%d : %s", k, v.toString())));
    }

    public static void dumpLowering(Compartment compartment){
        ConcurrentHashMap<Integer, CallableMethod> liftingCallable = compartment.getLoweringCallable();
        System.out.println("---------- Dump Lowering Callable -----------");
        liftingCallable.forEach((k, v) -> System.out.println(String.format("%d : %s", k, v.toString())));
    }

    public static void dumpCoreCallable(Player core){
        ConcurrentHashMap<Integer, CallableMethod> callable = core.coreCallable;
        System.out.println("---------- Dump Core Callable -----------");
        callable.forEach((k, v) -> System.out.println(String.format("%d : %s", k, v.toString())));
    }

    public static void dumpCompartments(){
        Registry reg = Registry.getRegistry();

        reg.getActiveCompartments().forEach((k,v) -> {
            System.out.println(k + " ::: " + v.hashCode() + " :: " + v.getClass());
        });
    }

    public static void dumpCores(){
        Registry reg = Registry.getRegistry();

        reg.getCores().forEach(k -> {
            System.out.println(k.hashCode() + " :: " + k.getClass());
        });
    }

    public static void dumpStacks(Compartment compartment){
        Registry reg = Registry.getRegistry();

        Stack<ArrayDeque<Relation>> stack = reg.getRollbackStacks().get(compartment.hashCode());

        //System.out.println("Stack size = " + stack.size());
        stack.forEach(k -> System.out.println("Stack -> " + k.size()));
    }
}
