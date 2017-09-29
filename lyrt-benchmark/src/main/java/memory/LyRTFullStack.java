package memory;

import binding.specimen.CoreObject;
import binding.specimen.R1;
import net.lyrt.Compartment;
import net.lyrt.Registry;

public class LyRTFullStack implements IMemoryOverhead{
//    int num;
    Registry reg = Registry.getRegistry();
    Compartment compartment;
    CoreObject[] cores;
//    public LyRTFullStack(int num){
//        this.num = num;
//    }

    @Override
    public void setup() {
        compartment = reg.newCompartment(Compartment.class);
        compartment.activate();
    }

    @Override
    public Object proceed(int numCore, int numRole) {
        cores = new CoreObject[numCore];

//        int numBinding = numRole/numCore;
        for(int i=0; i<numCore; i++){
            cores[i] = reg.newCore(CoreObject.class);
            cores[i].bind(R1.class);
//            for(int j=0; j<numBinding; j++){
//                cores[i].bind(R1.class);
//            }
//            cores[i].bind(R1.class);
//            core.bind(R1.class);
        }

        return cores;
    }

    public static void main(String... args) throws InterruptedException {
        Thread.sleep(5000);
        String[] str = new String[]{"-C10000", "-R10000"};
        new MemoryOverhead(args).run(new LyRTFullStack());
        Thread.sleep(2000);
    }

}
