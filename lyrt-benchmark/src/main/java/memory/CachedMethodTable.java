package memory;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;

public class CachedMethodTable implements IMemoryOverhead{
    Registry reg = Registry.getRegistry();
//    int num;

    Compartment compartment;
    CoreObject[] cores;
    R1[] roles;

//    public CachedMethodTable(int num){
//        this.num = num;
//    }

    public void setup(){
        compartment = reg.newCompartment(Compartment.class);
        compartment.activate();

//        cores = new CoreObject[num];
//        roles = new R1[num];
//
//        for(int i=0; i<num; i++){
//            cores[i] = new CoreObject();
//            roles[i] = new R1();
//        }
    }

    @Override
    public Object proceed(int numCore, int numRole) {
        cores = new CoreObject[numCore];
        roles = new R1[numCore];

//        int numBinding = numRole / numCore;
//        int idx;
        for(int i=0; i<numCore; i++){
            cores[i] = new CoreObject();
            roles[i] = new R1();
            reg.registerCoreCallable(cores[i].getCoreCallable(), cores[i], cores[i].getClass());
            reg.registerLiftingCallable(compartment, cores[i], roles[i], roles[i].getClass());
            reg.registerLoweringCallable(compartment, roles[i], roles[i].getClass());

//            for(int j=0; j<numBinding; j++) {
//                idx = i*numBinding + j;
//                roles[idx] = new R1();
//
//                reg.registerCoreCallable(cores[i].getCoreCallable(), cores[i], cores[i].getClass());
//                reg.registerLiftingCallable(compartment, cores[i], roles[idx], roles[idx].getClass());
//                reg.registerLoweringCallable(compartment, roles[idx], roles[idx].getClass());
//            }
        }

//        for(int i=0; i<num; i++){
//            reg.registerCoreCallable(cores[i].getCoreCallable(), cores[i], cores[i].getClass());
////            reg.registerCoreCallable(cores[i].getCoreCallable(), cores[i], cores[i].getClass());
////            reg.registerCoreCallable(cores[i].getCoreCallable(), cores[i], cores[i].getClass());
//            reg.registerLiftingCallable(compartment, cores[i], roles[i], roles[i].getClass());
//            reg.registerLoweringCallable(compartment, roles[i], roles[i].getClass());
//        }
        return cores;
    }

    public static void main(String... args) throws InterruptedException {
        Thread.sleep(5000);
        String[] str = new String[]{"-C10000", "-R10000"};
        new MemoryOverhead(args).run(new CachedMethodTable());
        Thread.sleep(2000);
    }
}
