package net.lyrt;

/**
 * Created by nguonly on 6/25/17.
 */
public interface IRole extends IPlayer{
    default  <T extends IRole> IRole bind(Class<T> roleClass, Object... args){
//        return _reg.bind(RelationEnum.DEEP_PLAY, this, roleClass, args);
        if(_reg.isUnanticipated){
            return _reg.roleBindRole(RelationEnum.DEEP_PLAY, this, roleClass.getName());
        }else {
            return _reg.bind(RelationEnum.DEEP_PLAY, this, roleClass, args);
        }
    }

    default  <T extends IRole> IRole rebind(Class<T> roleClass, Object... args){
        return _reg.rebind(RelationEnum.DEEP_PLAY, this, roleClass, args);
    }

    default   <T> T invokePlayer(String method, Class<T> retType, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        Object o = compartment.lowering(this);
        return compartment.sendLowering(o, method, retType, args);
    }

    default  <T> T invokeCore(String method, Class<T> retType, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        Player core = (Player)compartment.getCore(this);
        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", core.hashCode(), ms.toString());

        return _reg.invoke(core.coreCallable, null, retType, m, args);
    }

    default void invokeCore(String method, Object... args){
        invokeCore(method, void.class, args);
    }

    default  <T> T invokeCompartment(String method, Class<T> retType, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", compartment.hashCode(), ms.toString());

        return _reg.invoke(compartment.compartmentCallable, null, retType, m, args);
    }

    default void invokeCompartment(String method, Object... args){
        invokeCompartment(method, void.class, args);
    }

    default void invokeRel(Class<?> roleType, String method, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        Object[] cores = compartment.getCores(roleType);
        for(Object core : cores){
            ((IPlayer)core).invoke(method, args);
        }
    }

    default Object getCore(){
        Compartment compartment = _reg.getActiveCompartment();
        return compartment.getCore(this);
    }

    default Object[] getCores(){
        Compartment compartment = _reg.getActiveCompartment();
        return compartment.getCores(this.getClass());
    }

    default Object[] getCores(Class<?> roleClass){
        Compartment compartment = _reg.getActiveCompartment();
        return compartment.getCores(roleClass);
    }
}
