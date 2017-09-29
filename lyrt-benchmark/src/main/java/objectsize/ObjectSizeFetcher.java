package objectsize;

import com.sun.tools.attach.VirtualMachine;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import rewriting.RedefineClassAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;

public class ObjectSizeFetcher {
    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        try {
            ensureAgentLoaded();
        } catch (FailedToLoadAgentException e) {
            e.printStackTrace();
        }

        return instrumentation.getObjectSize(o);
    }

    private static volatile Instrumentation instrumentation = null;

    private static final int AGENT_LOAD_WAIT_TIME_SEC = 10;

    public static void agentmain(String agentArgs, Instrumentation inst) {
//        if (!inst.isRedefineClassesSupported()) {
//            System.out.println("Class redefinition not supported. Aborting.");
//            return;
//        }

        instrumentation = inst;
    }

    private static void ensureAgentLoaded() throws ObjectSizeFetcher.FailedToLoadAgentException {
        if (instrumentation != null) {
            // already loaded
            return;
        }

        // load the agent
        try {
            File agentJar = createAgentJarFile();

            // Loading an agent requires the PID of the JVM to load the agent to. Find out our PID.
            String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
            String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));

            // load the agent
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agentJar.getAbsolutePath(), "");
            vm.detach();
        } catch (Exception e) {
            throw new ObjectSizeFetcher.FailedToLoadAgentException(e);
        }

        // wait for the agent to load
        for (int sec = 0; sec < AGENT_LOAD_WAIT_TIME_SEC; sec++) {
            if (instrumentation != null) {
                // success!
                return;
            }

            try {
                System.out.println("Sleeping for 1 second while waiting for agent to load.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ObjectSizeFetcher.FailedToLoadAgentException();
            }
        }

        // agent didn't load
        throw new ObjectSizeFetcher.FailedToLoadAgentException();
    }

    private static File createAgentJarFile() throws IOException {
        File jarFile = File.createTempFile("agent", ".jar");
        jarFile.deleteOnExit();

        // construct a manifest that allows class redefinition
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttributes.put(new Attributes.Name("Agent-Class"), ObjectSizeFetcher.class.getName());
//        mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
//        mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");

        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
            // add the agent .class into the .jar
            JarEntry agent = new JarEntry(ObjectSizeFetcher.class.getName().replace('.', '/') + ".class");
            jos.putNextEntry(agent);

            // dump the class bytecode into the entry
//            ClassPool pool = ClassPool.getDefault();
//            CtClass ctClass = pool.get(RedefineClassAgent.class.getName());
//            jos.write(ctClass.toBytecode());
            jos.closeEntry();
        } //catch (CannotCompileException | NotFoundException e) {
            // Realistically this should never happen.
//            System.out.println("Exception while creating ObjectSizeFetcherAgent jar." +  e);
            //throw new IOException(e);
        //}

        return jarFile;
    }

    public static class FailedToLoadAgentException extends Exception {
        public FailedToLoadAgentException() {
            super();
        }

        public FailedToLoadAgentException(Throwable cause) {
            super(cause);
        }
    }
}
