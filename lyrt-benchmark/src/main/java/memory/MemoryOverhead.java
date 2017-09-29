package memory;

import java.lang.reflect.Array;

public class MemoryOverhead {

    int warm = 0;
    int iteration = 1;
    int numCore = 10000;
    int numRole = 10000;

    public MemoryOverhead(){

    }

    public MemoryOverhead(String... args){
        if(args.length>0){
            for(int i=0; i<args.length; i++){
//                System.out.println(args[i]);
                if(args[i].contains("-C")){ //core
                    numCore = Integer.parseInt(args[i].substring(2));
                }else if(args[i].contains("-R")){
                    numRole = Integer.parseInt(args[i].substring(2));
                }
            }
        }
    }

    public void run(IMemoryOverhead iMemoryOverhead){
        System.out.println(String.format("------------ %s --- Core : %d ----  Role : %d -------", iMemoryOverhead.getClass().getSimpleName(), numCore, numRole));
        System.out.println("Warm up : " + warm);
        for(int i=0; i<warm; i++) {
            proceed(iMemoryOverhead);
        }

//        Runtime rt = Runtime.getRuntime();
//        rt.gc();
//        rt.gc();
//        rt.gc();

        System.out.println("Iteration : " + iteration);
//        long[] results = new long[iteration];
        long sum = 0;
        for(int i=0; i<iteration; i++){
            sum += proceed(iMemoryOverhead);
        }

        String str = String.format("%s Core: %d Role: %d [Avg] : %d", iMemoryOverhead.getClass().getSimpleName(), numCore, numRole, sum/iteration);
        System.out.println(str);
    }

    private long proceed(IMemoryOverhead iMemoryOverhead){
        iMemoryOverhead.setup(); //setup phase

        Runtime rt = Runtime.getRuntime();
//        rt.gc();
//        rt.gc();
//        rt.gc();
//        sleep(1000);

        long memoryBefore = rt.totalMemory() - rt.freeMemory();
//        long memoryBefore = rt.freeMemory();

//        sleep(1000);

        iMemoryOverhead.proceed(numCore, numRole);

//        sleep(1000);

        long memoryAfter = rt.totalMemory() - rt.freeMemory();
//        long memoryAfter = rt.freeMemory();


//            System.out.println("Memory Before : " + memoryBefore);
//            System.out.println("Memory After  : " + (memoryAfter));
        long result = memoryAfter - memoryBefore;
        String str = String.format("%s : %d", iMemoryOverhead.getClass().getSimpleName(), result);
        System.out.println(str);
//            System.out.println("Overhead      : " + (memoryAfter - memoryBefore));

//        rt.gc();
//        rt.gc();
//        rt.gc();

        return result;
    }

    private void sleep(int milli){
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
