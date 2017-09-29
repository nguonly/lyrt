package net.lyrt.rollback;

import com.esotericsoftware.kryo.Kryo;
import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.Relation;
import net.lyrt.Role;
import net.lyrt.helper.DumpHelper;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by nguonly on 6/21/17.
 */
public class ControlUnit {
    public void checkpoint(Compartment compartment){
        Registry reg = Registry.getRegistry();
        long threadId = Thread.currentThread().getId();

        //1. shadow copy
        ArrayDeque<Relation> conf = copyRelations(compartment);

        //2. push the copied relation to the stack
        HashMap<Integer, Stack<ArrayDeque<Relation>>> hash = reg.getRollbackStacks();
        //Stack<ArrayDeque<Relation>> stack = hash.get(threadId);
        Stack<ArrayDeque<Relation>> stack = hash.get(compartment.hashCode());
        if(stack == null) stack = new Stack<>();
        stack.push(conf);
//        hash.put(threadId, stack);
        hash.put(compartment.hashCode(), stack);
//        System.out.println("Stack : " + conf);
    }

    public void checkpoint(){
        Registry reg = Registry.getRegistry();
        Compartment compartment = reg.getActiveCompartment();
        if(compartment == null) throw new RuntimeException("No active compartment was found.");

        checkpoint(compartment);
    }

//    public static void rollback(Compartment compartment, boolean restoreCompartment){
//        Registry reg = Registry.getRegistry();
//        ArrayDeque<Relation> relations = compartment.getRelations();
//
//        if(restoreCompartment){
//            Compartment currentCompartment = reg.getActiveCompartment();
//            if(currentCompartment == null) throw new RuntimeException("No active compartment was found.");
//
//            reg.destroyCompartment(currentCompartment);
//
//            //reactivate the previous compartment
//            compartment.activate();
//        }
//
//        relations.removeIf(p -> p.compartment.equals(compartment));
//
//        Stack<ArrayDeque<Relation>> stack = reg.getRollbackStacks().get(compartment.hashCode());
//
//        if(stack == null) return;
//
//        ArrayDeque<Relation> shadowRelation = stack.pop();
//        shadowRelation.stream()
//                .filter(p -> p.compartment.equals(compartment))
//                .forEach(k -> relations.add(k));
//
//        //Find cores to readjust callable
//        List<Relation> coreRelations = relations.stream()
//                .filter(p -> p.compartment.equals(compartment)).collect(Collectors.toList());
//
//        coreRelations.forEach(k -> reg.reRegisterCallable(compartment, k.core));
//
//    }

    public void rollback(){
        Registry reg = Registry.getRegistry();
        Compartment activeCompartment = reg.getActiveCompartment();
        if(activeCompartment == null) throw new RuntimeException("No active compartment was found.");

        rollback(activeCompartment);
    }

    public void rollback(Compartment activeCompartment){
        long threadId = Thread.currentThread().getId();
        rollback(threadId, activeCompartment);
    }

    public void rollback(Long threadId, Compartment activeCompartment){
        Registry reg = Registry.getRegistry();
        //Stack<ArrayDeque<Relation>> stack = reg.getRollbackStacks().get(threadId);
        Stack<ArrayDeque<Relation>> stack = reg.getRollbackStacks().get(activeCompartment.hashCode());
        if(stack == null) return;

        //restore previously saved application configuration
        ArrayDeque<Relation> shadowRelation = stack.pop();
//        System.out.println("Size of the serialized relation: " + shadowRelation.size());

        if(!shadowRelation.isEmpty()){
            Compartment compartment = (Compartment) shadowRelation.getFirst().compartment;

            if(!compartment.equals(activeCompartment)){
                reg.destroyCompartment(activeCompartment);
                compartment.activate();
            }

            ArrayDeque<Relation> relations = compartment.getRelations();

            //remove current application configuration
            relations.clear();
//            System.out.println("Remove current relation in a compartment");
//            DumpHelper.dumpRelations(compartment);

            compartment.loweringCallable.clear();
            compartment.liftingCallable.clear();
            compartment.compartmentCallable.clear();

            relations.addAll(shadowRelation);

            //Find cores to readjust callable
            Map<Object, Long> cores = relations.stream()
                    .collect(Collectors.groupingBy(Relation::getCore, Collectors.counting()));

            cores.forEach((k, v) -> reg.reRegisterCallable(compartment, k));
        }else{
            //in case of no binding
            activeCompartment.getRelations().clear();
            activeCompartment.liftingCallable.clear();
            activeCompartment.loweringCallable.clear();
        }
    }

    public ArrayDeque<Relation> copyRelations(Compartment compartment){
        Registry reg = Registry.getRegistry();
        Kryo kryo = reg.kryos.get();

        ArrayDeque<Relation> relations = compartment.getRelations();
        ArrayDeque<Relation> cloneRelations = new ArrayDeque<>();

        //1. clone the whole relations
        relations.forEach(k -> {
            Relation r = k.clone();
            cloneRelations.add(r);
        });

        List<Relation> deepRoleRelations = cloneRelations.stream()
                .filter(p -> !p.player.equals(p.core))
                .collect(Collectors.toList());

        //2. iterate over the clone relations to perform copy on roles and its players
        cloneRelations.forEach(k -> {
//            if(k.role instanceof Role){
                //3. deep copy role
//                Object role = isDeepCopy?kryo.copy(k.role):kryo.copyShallow(k.role);
            Object role = kryo.copy(k.role);

                //4. find players in relations and update. Expensive process
//                List<Relation> playerRelations = cloneRelations.stream()
//                        .filter(p -> p.core.equals(k.core)
//                                && p.player.equals(k.role)).collect(Collectors.toList());
//
//                playerRelations.forEach(z -> z.player = role);

                if(deepRoleRelations.size()>0){
                    deepRoleRelations.forEach(z -> {
                        if(z.player.equals(k.role)) {
                            z.player = role;
                        }
                    });
                }


                //5. update role to a copy version
                k.role = role;
//            }
        });

        return cloneRelations;
    }
}
