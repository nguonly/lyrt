package recovery;

import com.esotericsoftware.kryo.Kryo;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.openjdk.jmh.annotations.*;
import rop.ComponentCore;
import rop.ComponentRole;
import rop.RopCoreObject;
import rop.RopRole;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RollbackBaselineBenchmark {

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

                checkpoint(cores[i]);
            }
        }

        @TearDown
        public void tearDown(){
            hashStack.clear();
        }

        public void checkpoint(ComponentCore[] cores){

            HashMap<Integer, ComponentRole> hash;
            ArrayDeque<RecoveryBLRelation> clonedHash = new ArrayDeque<>();
            for(int i=0; i<cores.length; i++){
                hash = cores[i].getRoles();

                //clone to make it comparable to LyRT
                for(int j=0; j<hash.size(); j++){
                    Integer coreId = cores[i].hashCode();
                    RopRole clonedRole = (RopRole)kryo.copy(hash.get(coreId ));
                    RecoveryBLRelation relation = new RecoveryBLRelation();
                    relation.id=coreId;
                    relation.role = clonedRole;
                    clonedHash.add(relation);
                }


//                clonedHash.forEach((k, v) -> {
//                    RopRole copiedRole = (RopRole) kryo.copy(v);
//                    v = copiedRole;
////                    clonedHash.put(copiedRole.hashCode(), copiedRole);
//                });
            }

            Stack<ArrayDeque<RecoveryBLRelation>> stack = new Stack<>();
            stack.push(clonedHash);
            hashStack.put(cores[0].hashCode(), stack);
        }

        public void rollback(ComponentCore[] cores){
            Stack<ArrayDeque<RecoveryBLRelation>> stack = hashStack.get(cores[0].hashCode());
            ArrayDeque<RecoveryBLRelation> copiedHash = stack.pop();

            for(int i=0; i<cores.length; i++){
                HashMap<Integer, ComponentRole> hash = cores[i].getRoles();
                hash.clear();
                int coreId = cores[i].hashCode();
                List<RecoveryBLRelation> list = copiedHash.stream()
                        .filter(p -> p.id == coreId).collect(Collectors.toList());

                for(int j=0; j<list.size(); j++){
                    RecoveryBLRelation relation = list.get(j);
                    hash.put(relation.id, (ComponentRole) relation.role);
                }
            }
        }
    }


    @Benchmark
    public void rollbackBaseline(MyState s){
        s.rollback(s.cores[ARGS]);
    }
}
