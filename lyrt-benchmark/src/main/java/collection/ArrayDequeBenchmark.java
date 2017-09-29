package collection;

import binding.specimen.CoreObject;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by nguonly on 7/6/17.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ArrayDequeBenchmark {

    @State(Scope.Thread)
    public static class MyState{
        ArrayDeque<CoreObject> lists = new ArrayDeque<>();

        ArrayList<Integer> arrayList = new ArrayList<>();

        int NUM = 10;
        @Setup(Level.Invocation)
        public void setup(){

            for(int i=0; i<NUM; i++){
                CoreObject c = new CoreObject();
                lists.add(c);
                arrayList.add(c.hashCode());
            }

            Collections.shuffle(arrayList);
        }
    }

    @Benchmark
    public void search(MyState s){
        for(int i=0; i<s.NUM; i++) {
            int finalI = i;
            s.lists.stream().filter(p -> p.equals(s.arrayList.get(finalI))).findFirst();
        }
    }
}
