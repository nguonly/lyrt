package net.lyrt;

import es.uniovi.reflection.invokedynamic.interfaces.Callable;

import java.lang.reflect.Method;

/**
 * Created by nguonly on 5/12/17.
 */
public class CallableMethod {
    public String method;
    public Callable<?> callable;
    public Object invokingObject;
    public Method reflectedMethod; //fallback from reflection invocation

    public CallableMethod(){

    }

    public CallableMethod(String method, Object invokingObject, Callable<?> callable){
        this.method = method;
        this.invokingObject = invokingObject;
        this.callable = callable;
    }

    public CallableMethod(String method, Object invokingObject, Callable<?> callable, Method reflectedMethod){
        this(method, invokingObject, callable);
        this.reflectedMethod = reflectedMethod;
    }

    @Override
    public String toString(){
        return String.format("[%d] %s", invokingObject.hashCode(), method);
    }
}
