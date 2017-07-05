package net.lyrt;

/**
 * Created by nguonly on 5/12/17.
 */
public class Role extends Player {
    public <T extends Role> T bind(Class<T> roleClass, Object... args){
        return _reg.bind(RelationEnum.DEEP_PLAY, this, roleClass, args);
    }

    public <T extends Role> T rebind(Class<T> roleClass, Object... args){
        return _reg.rebind(RelationEnum.DEEP_PLAY, this, roleClass, args);
    }

    public  <T> T invokePlayer(String method, Class<T> retType, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        Object o = compartment.lowering(this);
        return compartment.sendLowering(o, method, retType, args);
    }

    public <T> T invokeCore(String method, Class<T> retType, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        Player core = (Player)compartment.getCore(this);
        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", core.hashCode(), ms.toString());

        return _reg.invoke(core.coreCallable, retType, m, args);
    }

    public void invokeCore(String method, Object... args){
        invokeCore(method, void.class, args);
    }

    public <T> T invokeCompartment(String method, Class<T> retType, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", compartment.hashCode(), ms.toString());

        return _reg.invoke(compartment.compartmentCallable, retType, m, args);
    }

    public void invokeCompartment(String method, Object... args){
        invokeCompartment(method, void.class, args);
    }

    public void invokeRel(Class<?> roleType, String method, Object... args){
        Compartment compartment = _reg.getActiveCompartment();
        if(compartment==null) throw new RuntimeException("No active compartment was found");

        Object[] cores = compartment.getCores(roleType);
        for(Object core : cores){
            ((Player)core).invoke(method, args);
        }
    }
}
