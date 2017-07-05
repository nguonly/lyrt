package binding.indy;

import java.lang.invoke.*;

/**
 * Created by nguonly on 6/30/17.
 */
public class IndyBootstrap {
    private static MethodHandle mhToString = null;

    public static CallSite callDispatch(MethodHandles.Lookup lookup, String name, MethodType methodType, Class<?> paramClass, String methodName) throws Throwable{

//        if(callSite != null)
//            return callSite;

        //mhToString  = lookup.findVirtual(paramClass, methodName, methodType.dropParameterTypes(0, 1));
        mhToString  = lookup.findVirtual(paramClass, methodName, methodType.dropParameterTypes(0, 1));

        //MethodType mtProfile = MethodType.methodType(methodType.returnType(),	MethodHandle.class, paramClass);
        //mhProfile = lookup.findStatic(MyBootstrap.class, "profile", mtProfile);
        //mhProfile = MethodHandles.insertArguments(mhProfile, 0, mhToString);


        //return callSite =  new MutableCallSite(mhToString);
        return new ConstantCallSite(mhToString);

    }

    public static CallSite anotherDispatch(MethodHandles.Lookup lookup, String name, MethodType methodType, Class<?> paramClass, String methodName) throws Throwable{

//        if(callSite != null)
//            return callSite;

        //mhToString  = lookup.findVirtual(paramClass, methodName, methodType.dropParameterTypes(0, 1));
        mhToString  = lookup.findVirtual(paramClass, methodName, methodType.dropParameterTypes(0, 1));

        //MethodType mtProfile = MethodType.methodType(methodType.returnType(),	MethodHandle.class, paramClass);
        //mhProfile = lookup.findStatic(MyBootstrap.class, "profile", mtProfile);
        //mhProfile = MethodHandles.insertArguments(mhProfile, 0, mhToString);


        //return callSite =  new MutableCallSite(mhToString);
        return new ConstantCallSite(mhToString);

    }
}
