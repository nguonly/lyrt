package javassist;

import java.io.IOException;

/**
 * Created by nguonly on 7/2/17.
 */
public class Inject1 {
    private String targetClass;
    private String targetMethod;
    Class clazz = null;

    public Inject1() {
        super();
    }

    public Inject1(String targetClass,String targetMethod) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }


    public void injectAround() {

        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass ctClazz = cp.get(targetClass);

            CtMethod method1 = ctClazz.getDeclaredMethod(targetMethod);
            method1.insertBefore("{ System.out.println(\"Code injected before method\"); }");
            method1.insertAfter("{ System.out.println(\"Code injected after method\"); }");
            ctClazz.writeFile();

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        catch(CannotCompileException e) {
            throw new RuntimeException(e);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }


    public byte[] injectBefore() {
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass ctClazz = cp.get(targetClass);
            ctClazz.defrost();
            ctClazz.detach();
            CtMethod method1 = ctClazz.getDeclaredMethod(targetMethod);
            method1.insertBefore("{ System.out.println(\"Reloaded class using HotSwapper  Reloaded class using HotSwapper -- One more Code injected before method\"); }");
            return ctClazz.toBytecode();

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        catch(CannotCompileException e) {
            throw new RuntimeException(e);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }


    }
}
