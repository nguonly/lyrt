package net.lyrt;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nguonly on 5/12/17.
 */
public class Player {
    public ConcurrentHashMap<Integer, CallableMethod> coreCallable = new ConcurrentHashMap<>();

    Registry _reg = Registry.getRegistry();

    public  <T> T invoke(String method, Class<T> retType, Object... args) {

        Compartment compartment = _reg.getActiveCompartment();
        int compartmentId = compartment != null ? compartment.hashCode() : 0;

//        String m =  getMethodKey(method, retType, args);
        MethodSig ms = getMethodSig(retType, method, args);
        String m = String.format("%d:%s", this.hashCode(), ms.toString());
        ConcurrentHashMap<Integer, CallableMethod> hash;
        if (compartmentId != 0) {
//            Object oo = compartment.lifting(this, method);
//            System.out.println(oo);
//            return compartment.send(oo, method, retType, args);
//            m = compartmentId + getMethodKey(method, retType, args);
//            m = getMethodSig(retType, method, args).toString();
//            //m = compartmentId + m;

            hash = compartment.liftingCallable;

        }else{

            hash = coreCallable;
        }

//        System.out.println(m);
        return _reg.invoke(hash, coreCallable, retType, m, args);

    }

    public void invoke(String method, Object... args){
        invoke(method, void.class, args);
    }

    public synchronized  <T extends Role> T bind(Class<T> roleClass, Object... args){
        return _reg.bind(RelationEnum.PLAY, this, roleClass, args);
    }

    public <T extends Role> T rebind(Class<T> roleClass, Object... args){
        return _reg.rebind(RelationEnum.PLAY, this, roleClass, args);
    }

    public void unbind(Class<?> roleType){
        _reg.unbind(RelationEnum.PLAY, this, roleType);
    }

    public void transfer(Class<?> roleType, Player toCore){
        _reg.transfer(this, toCore, roleType);
    }

//    protected String getMethodKey(String method, Class<?> retType, Object... args){
//        String strRetType = retType.getTypeName();
//
//        String paramTypesStr = "";
//        for(Object o: args){
//            paramTypesStr += o.getClass().getName() + ", ";
//        }
//        if (args.length > 0) {
//            paramTypesStr = paramTypesStr.substring(0, paramTypesStr.length() - 2);
//        }
//        return String.format("%s %s(%s)", strRetType, method, paramTypesStr);
//    }

    protected MethodSig getMethodSig(Class<?> retType, String methodName, Object... args){
        Class<?>[] paramTypes = new Class[args.length];
        for(int i=0; i<args.length; i++){
//            Class<?> cls = args[i].getClass();
//            if(cls.isPrimitive()) cls = BoxingHelper.primitiveOrVoidObjectClass.get(cls);
            paramTypes[i] = args[i].getClass();
        }

        return new MethodSig(retType, methodName, paramTypes);
    }



//    default <T> T newCore(HashMap<Integer, CallableMethod> coreCallable, Class<T> coreClass, Object... args){
//        T core = ReflectionHelper.newInstance(coreClass, args);
//
//        registerCallable(coreCallable, core, coreClass);
//        return core;
//    }
}
