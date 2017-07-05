package net.lyrt.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nguonly on 6/19/17.
 */
public class BoxingHelper {
    public static Map<Class<?>,Class<?>> primitiveOrVoidObjectClass = new HashMap<Class<?>, Class<?>>();
    public static Map<String,Class<?>> descriptorPrimitive = new HashMap<String, Class<?>>();


    static {

        primitiveOrVoidObjectClass.put(int.class, Integer.class);
        primitiveOrVoidObjectClass.put(short.class, Short.class);
        primitiveOrVoidObjectClass.put(double.class, Double.class);
        primitiveOrVoidObjectClass.put(float.class, Float.class);
        primitiveOrVoidObjectClass.put(long.class, Long.class);
        primitiveOrVoidObjectClass.put(boolean.class, Boolean.class);
        primitiveOrVoidObjectClass.put(char.class, Character.class);
        primitiveOrVoidObjectClass.put(byte.class, Byte.class);
        primitiveOrVoidObjectClass.put(void.class, Void.class);

        primitiveOrVoidObjectClass.put(int[].class, Integer[].class);
        primitiveOrVoidObjectClass.put(short[].class, Short[].class);
        primitiveOrVoidObjectClass.put(double[].class, Double[].class);
        primitiveOrVoidObjectClass.put(float[].class, Float[].class);
        primitiveOrVoidObjectClass.put(long[].class, Long[].class);
        primitiveOrVoidObjectClass.put(boolean[].class, Boolean[].class);
        primitiveOrVoidObjectClass.put(char[].class, Character[].class);
        primitiveOrVoidObjectClass.put(byte[].class, Byte[].class);

        descriptorPrimitive.put("int", int.class);
        descriptorPrimitive.put("short", short.class);
        descriptorPrimitive.put("double", double.class);
        descriptorPrimitive.put("float", float.class);
        descriptorPrimitive.put("long", long.class);
        descriptorPrimitive.put("boolean", boolean.class);
        descriptorPrimitive.put("char", char.class);
        descriptorPrimitive.put("byte", byte.class);
        descriptorPrimitive.put("void", void.class);

    }
}
