package memory;

import rop.ComponentCore;
import rop.ComponentRole;

public class MemoryBaseline implements IMemoryOverhead{
//    int numCore;
//    int numRole;
//
//    public MemoryBaseline(int numCore, int numRole){
//        this.numCore = numCore;
//        this.numRole = numRole;
//    }

    @Override
    public void setup() {

    }

    @Override
    public Object proceed(int numCore, int numRole) {
        ComponentCore[] cores = new ComponentCore[numCore];
        ComponentRole[] roles = new ComponentRole[numCore];

//        int numBinding = numRole/numCore;
        int idx;
        for(int i=0; i<numCore; i++){
            cores[i] = new ComponentCore();
            roles[i] = new ComponentRole();
            cores[i].addRole(roles[i]);
//            for(int j=0; j<numBinding; j++){
//                idx = i*numBinding + j;
//                roles[idx] = new ComponentRole();
//                cores[i].addRole(roles[idx]);
//            }
        }

        return cores;
    }

    public static void main(String... args) throws InterruptedException {
        Thread.sleep(5000);
        String[] str = new String[]{"-C10000", "-R10000"};
        new MemoryOverhead(args).run(new MemoryBaseline());
        Thread.sleep(2000);
    }
}
