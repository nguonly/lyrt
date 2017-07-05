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
 * Created by nguonly on 6/25/17.
 */
public interface ICompartment extends IPlayer {
//     ArrayDeque<Relation> _relations = new ArrayDeque<>();
//
//    public ConcurrentHashMap<Integer, CallableMethod> compartmentCallable = new ConcurrentHashMap<>();
//    //Id: Integer (object + method)
//    public ConcurrentHashMap<Integer, CallableMethod> liftingCallable = new ConcurrentHashMap<>();
//
//    public ConcurrentHashMap<Integer, CallableMethod> loweringCallable = new ConcurrentHashMap<>();
//
//    public ConcurrentHashMap<Integer, CallableMethod> consistencyCallable = new ConcurrentHashMap<>();
//
//    private Registry _reg = Registry.getRegistry();
//
//    default InitBindingBlock initBinding(){
//        return new InitBindingBlock(this);
//    }
//
//    default void activate(){
//        _reg.activateCompartment(this);
//    }
//
//    default void deactivate(){
//        deactivate(true);
//    }
//
//    default void deactivate(boolean doCheckpoint){
//        if(doCheckpoint) ControlUnit.checkpoint(this);
//        _reg.deactivateCompartment(this);
//    }
//
//    default void addRelation(Relation relation){
//        _relations.add(relation);
//    }
//
//    default ArrayDeque<Relation> getRelations(){ return _relations; }
//
//    default ConcurrentHashMap<Integer, CallableMethod> getLiftingCallable() { return liftingCallable;}
//
//    default ConcurrentHashMap<Integer, CallableMethod> getLoweringCallable(){return loweringCallable;}
//
//    default Object lifting(Object player, String method){
//        final Comparator<Relation> depthComparator = Comparator.comparingInt(p -> p.depth);
//        Optional<Relation> optRelation = _relations.stream().filter(p -> p.core == player).max(depthComparator);
//        Object obj = optRelation.isPresent()?optRelation.get().role:player;
//        return obj;
//    }
//
//    default Object lowering(Object role){
//        Optional<Relation> optRelation = _relations.stream().filter(p -> p.role == role).findFirst();
//        return optRelation.map(relation -> relation.player).orElse(null);
//    }
//
//    default int getDepth(Object role){
//        Optional<Relation> optRelation = _relations.stream().filter(p -> p.role == role).findFirst();
//        return optRelation.isPresent()?optRelation.get().depth:0;
//    }
//
//    default Object getCore(Object role){
//        Optional<Relation> optRelation = _relations.stream().filter(p -> p.role == role).findFirst();
//        return optRelation.map(relation -> relation.core).orElse(null);
//    }
//
//    default Object[] getCores(Class<?> roleType){
//        List<Object> list = _relations.stream().filter(p -> p.role.getClass().equals(roleType)).map(p->p.core).collect(Collectors.toList());
//        return list.toArray();
//    }
//
//    default <T> T invoke(String method, Class<T> retType, Object... args){
//
//        MethodSig ms = getMethodSig(retType, method, args);
//        String m = String.format("%d:%s", this.hashCode(), ms.toString());
//        return _reg.invoke(this.compartmentCallable, retType, m, args);
//    }
//
//    default void invoke(String method, Object... args){
//        invoke(method, void.class, args);
//    }
//
//    /**
//     * Invoke a message
//     * @param obj receiver
//     * @param method method name
//     * @param retType return type
//     * @param args arguements
//     * @param <T> type
//     * @return result of the method invocation
//     */
////    public <T> T send(Object obj, String method, Class<T> retType, Object... args){
////        //String m = String.format("%d:%d:%s", this.hashCode(), obj.hashCode(), getMethodKey(method, retType, args));
////        String m = String.format("%d:%s", this.hashCode(), getMethodKey(method, retType, args));
////        CallableMethod cm = liftingCallable.get(m.hashCode());
//////        System.out.println(m + " " + m.hashCode());
//////        coreCallable.forEach((k, v) -> {
//////            System.out.println(k);
//////        });
////        Object objRet = cm.callable.invoke(cm.invokingObject, args);
////        if(retType!=null && !retType.isAssignableFrom(void.class) && !retType.isAssignableFrom(Void.class)) {
////            return retType.cast(objRet);
////        }
////        return null;
////    }
//
//    default  <T> T sendLowering(Object obj, String method, Class<T> retType, Object... args){
//        //String m = String.format("%d:%d:%s", this.hashCode(), obj.hashCode(), getMethodKey(method, retType, args));
//        String m = String.format("%d:%s", obj.hashCode(), getMethodSig(retType, method, args).toString());
//
//        return _reg.invoke(loweringCallable, retType, m, args);
//    }
}
