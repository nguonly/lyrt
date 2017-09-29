package memory;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;

import java.util.HashMap;

public class MemoryOverheadTest {
    static Registry reg = Registry.getRegistry();

    public static void main(String... args){
        CoreObject[] o;
        Compartment comp = reg.newCompartment(Compartment.class);
        comp.activate();
//        o = makeCores(10000);
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

//        makeObject(10000);
        o = makeCores(10000);
//        bind(o);

        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.println("Memory Before : " + memoryBefore);
        System.out.println("Memory After  : " + ( memoryAfter));
        System.out.println("Overhead      : " + (memoryAfter - memoryBefore));
    }


    public static HashMap<Object, Object> makeObject(int num){
        //Registry reg = Registry.getRegistry();
        CoreObject[] objs = new CoreObject[num];
        R1[] roles = new R1[num];
        HashMap<Object, Object> hash = new HashMap<>();

        for(int i=0; i<num; i++){
            objs[i] = new CoreObject();
            roles[i] = new R1();
            hash.put(objs[i], roles[i]);
        }

        return hash;
    }

    public static CoreObject[] makeCores(int num){

        CoreObject[] objs = new CoreObject[num];

        for(int i=0; i<num; i++){
            objs[i] = reg.newCore(CoreObject.class);
            objs[i].bind(R1.class);
        }

        return objs;
    }

    public static void bind(CoreObject[] cores){
        int num = cores.length;

        for(int i=0; i< num; i++){
            cores[i].bind(R1.class);
        }
    }
}
