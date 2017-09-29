package binding;

import binding.specimen.CoreObject;
import com.esotericsoftware.kryo.Kryo;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import recovery.RecoveryBLRelation;
import rop.ComponentCore;
import rop.ComponentRole;
import rop.RopCoreObject;
import rop.RopRole;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CheckpointBaselineBenchmark {

    @Param({"0", "1", "2", "3", "4"})
    public int ARGS; //adjust according to params

    static int params[] = new int[]{10, 100, 1000, 5000, 10000};

    @State(Scope.Thread)
    public static class MyState{

        Kryo kryo = new Kryo();
        RopCoreObject[][] cores = new RopCoreObject[params.length][];

        HashMap<Integer, Stack<ArrayDeque<RecoveryBLRelation>>> hashStack = new HashMap<>();

        @Setup(Level.Invocation)
        public void setup(){
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

            hashStack = new HashMap<>();

            for(int i=0; i<params.length; i++){
                cores[i] = new RopCoreObject[params[i]];
                for(int j=0; j<params[i]; j++){
                    cores[i][j] = new RopCoreObject();
                    cores[i][j].addRole(new RopRole());
                }
            }
        }

        @TearDown
        public void tearDown(){
            hashStack.clear();
        }

        public HashMap<Integer, ComponentRole> checkpoint(ComponentCore[] cores){

            HashMap<Integer, ComponentRole> hash = new HashMap<>();
            ArrayDeque<RecoveryBLRelation> clonedHash = new ArrayDeque<>();

            for(int i=0; i<cores.length; i++){
                hash = cores[i].getRoles();
//                clonedHash = new HashMap<>();

                //clone to make it comparable to LyRT
                for(int j=0; j<hash.size(); j++){
                    Integer coreId = cores[i].hashCode();
                    RopRole clonedRole = (RopRole)kryo.copy(hash.get(coreId));
                    RecoveryBLRelation relation = new RecoveryBLRelation();
                    relation.id=coreId;
                    relation.role = clonedRole;
                    clonedHash.add(relation);
                }
            }

            Stack<ArrayDeque<RecoveryBLRelation>> stack = new Stack<>();
            stack.push(clonedHash);
            hashStack.put(cores[0].hashCode(), stack);

            return hash;
        }

//        private ArrayDeque<RecoveryBLRelation> copy(HashMap<Integer, ComponentRole> roles){
//            ArrayDeque<RecoveryBLRelation> clonedRelation = new ArrayDeque<>();
//            roles.forEach((k, v) -> {
//                RopRole clonedRole = new RopRole();
//                RecoveryBLRelation r = new RecoveryBLRelation();
//                r.id = k;
//                r.role = clonedRole;
//                clonedRelation.add(r);
//            });
//
//            return clonedRelation;
//        }
    }

    @Benchmark
    public void checkpointBaseline(MyState s){
        s.checkpoint(s.cores[ARGS]);
    }
}
