package net.lyrt;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nguonly on 5/12/17.
 */
public class Player implements IPlayer{
//    public ConcurrentHashMap<Integer, CallableMethod> coreCallable = new ConcurrentHashMap<>();
//
//    Registry _reg = Registry.getRegistry();
//
//    public  <T> T invoke(String method, Class<T> retType, Object... args) {
//
//        Compartment compartment = _reg.getActiveCompartment();
//        int compartmentId = compartment != null ? compartment.hashCode() : 0;
//
//        MethodSig ms = getMethodSig(retType, method, args);
//        String m = String.format("%d:%s", this.hashCode(), ms.toString());
//        ConcurrentHashMap<Integer, CallableMethod> hash;
//        if (compartmentId != 0) {
//            hash = compartment.liftingCallable;
//        }else{
//
//            hash = coreCallable;
//        }
//
//        return _reg.invoke(hash, coreCallable, retType, m, args);
//
//    }
//
//    public void invoke(String method, Object... args){
//        invoke(method, void.class, args);
//    }
//
//    public  <T extends Role> T bind(Class<T> roleClass, Object... args){
//        return _reg.bind(RelationEnum.PLAY, this, roleClass, args);
//    }
//
//    public <T extends Role> T rebind(Class<T> roleClass, Object... args){
//        return _reg.rebind(RelationEnum.PLAY, this, roleClass, args);
//    }
//
//    public void unbind(Class<?> roleType){
//        _reg.unbind(RelationEnum.PLAY, this, roleType);
//    }
//
//    public void transfer(Class<?> roleType, Player toCore){
//        _reg.transfer(this, toCore, roleType);
//    }
//
//    protected MethodSig getMethodSig(Class<?> retType, String methodName, Object... args){
//        Class<?>[] paramTypes = new Class[args.length];
//        for(int i=0; i<args.length; i++){
//            paramTypes[i] = args[i].getClass();
//        }
//
//        return new MethodSig(retType, methodName, paramTypes);
//    }
}
