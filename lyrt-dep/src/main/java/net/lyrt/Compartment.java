package net.lyrt;

import net.lyrt.block.InitBindingBlock;
import net.lyrt.rollback.ControlUnit;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by nguonly on 5/12/17.
 */
public class Compartment implements IPlayer {
    private ArrayDeque<Relation> _relations = new ArrayDeque<>();

    public ConcurrentHashMap<Integer, CallableMethod> compartmentCallable = new ConcurrentHashMap<>();
    //Id: Integer (object + method)
    public ConcurrentHashMap<Integer, CallableMethod> liftingCallable = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Integer, CallableMethod> loweringCallable = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Integer, CallableMethod> consistencyCallable = new ConcurrentHashMap<>();

    private Registry _reg = Registry.getRegistry();

    public InitBindingBlock initBinding(){
        return new InitBindingBlock(this);
    }

    public void activate(){
        _reg.activateCompartment(this);
    }

    public void deactivate(){
        deactivate(true);
    }

    public void deactivate(boolean doCheckpoint){
        if(doCheckpoint) {
            ControlUnit controlUnit = new ControlUnit();
            controlUnit.checkpoint(this);
            //ControlUnit.checkpoint(this);
        }
        _reg.deactivateCompartment(this);
    }

    public void addRelation(Relation relation){
        _relations.add(relation);
    }

    public ArrayDeque<Relation> getRelations(){ return _relations; }

    public ConcurrentHashMap<Integer, CallableMethod> getLiftingCallable() { return liftingCallable;}

    public ConcurrentHashMap<Integer, CallableMethod> getLoweringCallable(){return loweringCallable;}

    public Object lifting(Object player, String method){
        final Comparator<Relation> depthComparator = Comparator.comparingInt(p -> p.depth);
        Optional<Relation> optRelation = _relations.stream().filter(p -> p.core == player).max(depthComparator);
        Object obj = optRelation.isPresent()?optRelation.get().role:player;
        return obj;
    }

    public Object lowering(Object role){
        Optional<Relation> optRelation = _relations.stream().filter(p -> p.role == role).findFirst();
        return optRelation.map(relation -> relation.player).orElse(null);
    }

    public int getDepth(Object role){
        Optional<Relation> optRelation = _relations.stream().filter(p -> p.role == role).findFirst();
        return optRelation.isPresent()?optRelation.get().depth:0;
    }

    public Object getCore(Object role){
        Optional<Relation> optRelation = _relations.stream().filter(p -> p.role == role).findFirst();
        return optRelation.map(relation -> relation.core).orElse(null);
    }

    public Object[] getCores(Class<?> roleType){
        List<Object> list = _relations.stream().filter(p -> p.role.getClass().equals(roleType)).map(p->p.core).collect(Collectors.toList());
        return list.toArray();
    }

    public <T> T invoke(String method, Class<T> retType, Object... args){
        Compartment compartment = _reg.getActiveCompartment();

        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", this.hashCode(), ms.toString());

        if(compartment==null || compartment.equals(this))
            return _reg.invoke(this.compartmentCallable, null, retType, m, args);
        else
            return _reg.invoke(compartment.liftingCallable, this.compartmentCallable, retType, m, args);
    }

    public void invoke(String method, Object... args){
        invoke(method, void.class, args);
    }

    public Object[] getRoles(Class<?> roleType){
        List<Object> list = _relations.stream()
                .filter(p -> p.role.getClass().equals(roleType)).map(p->p.role).collect(Collectors.toList());
        return list.toArray();
    }

    /**
     * Invoke a message
     * @param obj receiver
     * @param method method name
     * @param retType return type
     * @param args arguements
     * @param <T> type
     * @return result of the method invocation
     */
//    public <T> T send(Object obj, String method, Class<T> retType, Object... args){
//        //String m = String.format("%d:%d:%s", this.hashCode(), obj.hashCode(), getMethodKey(method, retType, args));
//        String m = String.format("%d:%s", this.hashCode(), getMethodKey(method, retType, args));
//        CallableMethod cm = liftingCallable.get(m.hashCode());
////        System.out.println(m + " " + m.hashCode());
////        coreCallable.forEach((k, v) -> {
////            System.out.println(k);
////        });
//        Object objRet = cm.callable.invoke(cm.invokingObject, args);
//        if(retType!=null && !retType.isAssignableFrom(void.class) && !retType.isAssignableFrom(Void.class)) {
//            return retType.cast(objRet);
//        }
//        return null;
//    }

    public <T> T sendLowering(Object obj, String method, Class<T> retType, Object... args){
        //String m = String.format("%d:%d:%s", this.hashCode(), obj.hashCode(), getMethodKey(method, retType, args));
        String m = String.format("%d:%s", obj.hashCode(), getMethodSig(retType, method, args).toString());

        return _reg.invoke(loweringCallable, null, retType, m, args);
    }
}
