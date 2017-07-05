package net.lyrt.helper;

import net.lyrt.CallableMethod;
import net.lyrt.Compartment;
import net.lyrt.Player;
import net.lyrt.Relation;

import java.util.ArrayDeque;
import java.util.List;
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
}
