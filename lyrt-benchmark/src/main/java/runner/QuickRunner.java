package runner;

import binding.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import rewriting.ByteBuddyRedefinitionBenchmark;
import rewriting.JavassistRedefinitionBenchmark;

/**
 * Created by nguonly on 6/26/17.
 */
public class QuickRunner {
    /**
     * A wildcard for the identification of a benchmark by JMH.
     */
    private static final String WILDCARD = ".*";

    /**
     * This class is not supposed to be constructed.
     */
    private QuickRunner() {
        throw new UnsupportedOperationException();
    }

    public static void main(String... args) throws RunnerException{
        new Runner(new OptionsBuilder()
//                .include(WILDCARD + MethodInvocationBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + CheckpointBenchmark.class.getSimpleName() + WILDCARD)
////                .include(WILDCARD + CheckpointDeepCopyBenchmark.class.getSimpleName() + WILDCARD) //consider later
//                .include(WILDCARD + Rollback10Benchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + Rollback100Benchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + Rollback1000Benchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + Rollback10000Benchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + BindingBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + UnbindingBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + TransferBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + UnanticipatedBindingBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + UnanticipatedUnbindingBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + UnanticipatedTransferBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + ByteBuddyRedefinitionBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + JavassistRedefinitionBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + WithoutConsistencyInvocationBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + WithConsistencyInvocationBenchmark.class.getSimpleName() + WILDCARD)
                .include(WILDCARD + LoweringBenchmark.class.getSimpleName() + WILDCARD)
//                .include(WILDCARD + LoweringWithDelegationBenchmark.class.getSimpleName() + WILDCARD)
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .jvmArgs("-server")
                .threads(1)
//                .shouldDoGC(true)
                .build()
        ).run();
    }
}
