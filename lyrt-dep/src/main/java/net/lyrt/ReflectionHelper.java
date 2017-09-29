package net.lyrt;

import net.lyrt.unanticipation.ClassReloader;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguonly on 5/12/17.
 */
public class ReflectionHelper {
    public static <T> T newInstance(Class<T> clazz, Object... args){
        try {
            T obj;

            Class<?>[] paramTypes;
            Class<T> loadingClass = clazz;

            if(Registry.getRegistry().isUnanticipated && isRoleType(clazz)) {
                //Dynamic reload the class
                loadingClass = (Class<T>) new ClassReloader().loadClass(clazz);
            }

            if (args != null && args.length > 0) {
                paramTypes = getParameterTypes(args);
//                Constructor<T> constr = (Constructor<T>)dynClass.getConstructor(paramTypes);
                Constructor<T> constr = loadingClass.getConstructor(paramTypes);
                obj = constr.newInstance(args);
            } else {
//                obj = dynClass.newInstance();
                obj = loadingClass.newInstance();
            }

            return obj;
        }catch(Throwable e){
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isRoleType(Class<?> clazz){
        Class<?>[] clazzes = clazz.getInterfaces();
        for(int i=0; i<clazzes.length; i++){
            if(clazzes[i].equals(IRole.class)) return true;
        }

        return false;
    }

    public static List<Class<?>> getAllSuperClassesAndInterfaces(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?>[] ifs = clazz.getInterfaces();
        //add itself
        classes.add(clazz);

        //super classes
        while (true) {
            Class<?> superClass = clazz.getSuperclass();
            classes.add(superClass);
            if (superClass.equals(Object.class)) break;
            clazz = superClass;
        }

        //get all interfaces
        for (Class<?> i : ifs) {
            classes.add(i);
        }

        return classes;
    }

    private static Class<?>[] getParameterTypes(Object... args) {
        Class<?>[] params=new Class<?>[args.length];
        for (int i=0;i<args.length;i++){
            Class<?> cls = args[i].getClass();
            //fixing the passing of bytebuddy subclass to a constrcutor resulting not found constructor
            if(cls.getSimpleName().contains("ByteBuddy")){
                params[i] = cls.getSuperclass();
            }else {
                params[i] = args[i].getClass();
            }
        }
        return params;
    }
}
