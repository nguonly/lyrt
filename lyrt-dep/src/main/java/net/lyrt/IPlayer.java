package net.lyrt;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nguonly on 6/25/17.
 */
public interface IPlayer {
    ConcurrentHashMap<Integer, CallableMethod> coreCallable = new ConcurrentHashMap<>();

    Registry _reg = Registry.getRegistry();

    default ConcurrentHashMap<Integer, CallableMethod> getCoreCallable(){ return coreCallable;}

    default  <T> T invoke(String method, Class<T> retType, Object... args) {

        Compartment compartment = _reg.getActiveCompartment();
        int compartmentId = compartment != null ? compartment.hashCode() : 0;

        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", this.hashCode(), ms.toString());
        ConcurrentHashMap<Integer, CallableMethod> hash;
        if (compartmentId != 0) {
            hash = compartment.liftingCallable;
        }else{
            hash = coreCallable;
        }

        return _reg.invoke(hash, coreCallable, retType, m, args);
    }

    default void invoke(String method, Object... args){
        invoke(method, void.class, args);
    }

    default <T extends IRole> IRole bind(Class<T> roleClass, Object... args){
        if(_reg.isUnanticipated){
            Compartment compartment = _reg.getActiveCompartment();
            return _reg.bind(RelationEnum.PLAY, compartment.hashCode(), this.hashCode(), roleClass.getName());
        }else {
            return _reg.bind(RelationEnum.PLAY, this, roleClass, args);
        }
    }

    default <T extends IRole> IRole rebind(Class<T> roleClass, Object... args){
        return _reg.rebind(RelationEnum.PLAY, this, roleClass, args);
    }

    default void unbind(Class<?> roleType){
        _reg.unbind(RelationEnum.PLAY, this, roleType);
    }

    default void unbindAll(){
        _reg.unbindAll(this);
    }

    default void transfer(Class<?> roleType, IPlayer toCore){
        _reg.transfer(this, toCore, roleType);
    }

    default MethodSig getMethodSig(Class<?> retType, String methodName, Object... args){
        if(args == null) return new MethodSig(retType, methodName, new Class<?>[0]);
        Class<?>[] paramTypes = new Class[args.length];
        for(int i=0; i<args.length; i++){
            paramTypes[i] = args[i].getClass();
        }

        return new MethodSig(retType, methodName, paramTypes);
    }
}
