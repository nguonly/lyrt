package net.lyrt;

import net.lyrt.helper.BoxingHelper;

import java.util.Arrays;

/**
 * Created by nguonly on 6/19/17.
 * Method Sigature resembling Method class
 */
public class MethodSig {
    private String methodName;
    private static Class<?>[] ptypesnull ={};
    private Class<?> clazz = null;
    private final Class<?> rtype;
    private Class<?>[] parametersType;

    public MethodSig(Class<?> clazz, Class<?> rtype, String methodName, Class<?>[]parametersType){
        this.methodName = methodName;
        this.clazz = clazz;
        this.rtype = rtype;
        this.parametersType = parametersType;

    }

    public MethodSig(Class<?> rtype, String methodName, Class<?>[] parametersType){
        this.methodName = methodName;
        this.rtype = rtype;
        this.parametersType = new Class<?>[parametersType.length];
        for(int i=0; i<parametersType.length; i++){
            if(parametersType[i].isPrimitive()){
                this.parametersType[i] = BoxingHelper.primitiveOrVoidObjectClass.get(parametersType[i]);
            }else{
                this.parametersType[i] = parametersType[i];
            }
        }
//        this.parametersType = parametersType;
    }

    /**
     * Gets the class the defined method belongs to.
     * @return Gets the class the defined method belongs to. If it is static, its value is <code>null</code>
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * Gets the return type of the member.
     * @return The return type of the member.
     */
    public Class<?> getRtype() {
        return rtype;
    }

    /**
     * Gets the type of the parameters of the member
     * @return Type of the parameters of the member
     */
    public Class<?>[] getParametersType() {
        return parametersType;
    }

    public String getMethodName(){return methodName;}

    /**
     * Tells whether the member is static.
     * @return <code>true</code> if the member is static; <code>false</code> otherwise.
     */
    public boolean isStatic(){
        if(clazz == null)
            return true;
        else
            return false;
    }

    @Override
    public String toString(){
        return String.format("%s %s %s", this.rtype.getSimpleName(), this.methodName, Arrays.deepToString(this.getParametersType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + methodName.hashCode();
        result = prime * result + Arrays.hashCode(parametersType);
        result = prime * result + rtype.hashCode();
        return result;
    }
}
