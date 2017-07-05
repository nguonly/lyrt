package net.lyrt;

import es.uniovi.reflection.invokedynamic.interfaces.Callable;

/**
 * Created by nguonly on 5/12/17.
 */
public class CallableMethod {
    public String method;
    public Callable<?> callable;
    public Object invokingObject;

    public CallableMethod(){

    }

    public CallableMethod(String method, Object invokingObject, Callable<?> callable){
        this.method = method;
        this.invokingObject = invokingObject;
        this.callable = callable;
    }

    @Override
    public String toString(){
        return String.format("[%d] %s", invokingObject.hashCode(), method);
    }
}
