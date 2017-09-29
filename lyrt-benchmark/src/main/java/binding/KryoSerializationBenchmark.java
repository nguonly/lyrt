package binding;

import binding.specimen.CoreObject;
import com.esotericsoftware.kryo.Kryo;
import net.lyrt.Registry;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class KryoSerializationBenchmark {

    @Param({"0", "1", "2", "3", "4"})
    private int ARGS; //align with params

    private static int[] params = new int[]{10, 100, 1000, 5000, 10000};

    @State(Scope.Thread)
    public static class MyState{
        Kryo kryo;
        CoreObject[][] cores;

        @Setup(Level.Invocation)
        public void setup(){
            kryo = new Kryo();
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

            int N = params.length;
            cores = new CoreObject[N][];

            for(int i=0; i<N; i++){
                cores[i] = new CoreObject[params[i]];
            }
        }

        public Object[] copy(boolean isDeepCopy, int paramIdx){
            int num = params[paramIdx];
            Object[] objects = new Object[num];

            if(isDeepCopy) {
                for (int i = 0; i < num; i++) {
                    objects[i] = kryo.copy(cores[paramIdx][i]);
                }
            }else{
                for(int i=0; i<num; i++){
                    objects[i] = kryo.copyShallow(cores[paramIdx][i]);
                }
            }

            return objects;
        }
    }

    @Benchmark
    public void shallowCopy(MyState s){
        s.copy(false, ARGS);
    }

    @Benchmark
    public void deepCopy(MyState s){
        s.copy(true, ARGS);
    }
}
