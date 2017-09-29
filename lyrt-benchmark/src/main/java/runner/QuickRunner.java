package runner;

import adaptation.LyRTAdaptationBenchmark;
import binding.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import recovery.RollbackBaselineBenchmark;
import rewriting.JavassistAdaptationBenchmark;

/**
 * Created by nguonly on 6/26/17.
 */
public class QuickRunner {

    /**
     * This class is not supposed to be constructed.
     */
    private QuickRunner() {
        throw new UnsupportedOperationException();
    }

    public static void main(String... args) throws RunnerException{
        new Runner(new OptionsBuilder()
//                .include( MethodInvocationBenchmark.class.getSimpleName())
//                .include(LyRTMultipleCalls.class.getSimpleName())
                .include(CheckpointBenchmark.class.getSimpleName())
                .include(CheckpointBaselineBenchmark.class.getSimpleName())
                .include(RollbackBaselineBenchmark.class.getSimpleName())
//                .include(CheckpointDeepCopyBenchmark.class.getSimpleName()) //consider later
                .include(Rollback10Benchmark.class.getSimpleName())
                .include(Rollback100Benchmark.class.getSimpleName())
                .include(Rollback1000Benchmark.class.getSimpleName())
                .include(Rollback5000Benchmark.class.getSimpleName())
                .include(Rollback10000Benchmark.class.getSimpleName())
//                .include(BindingBenchmark.class.getSimpleName())
//                .include(UnbindingBenchmark.class.getSimpleName())
//                .include(TransferBenchmark.class.getSimpleName() )
//                .include(UnanticipatedBindingBenchmark.class.getSimpleName())
//                .include(UnanticipatedUnbindingBenchmark.class.getSimpleName())
//                .include(UnanticipatedTransferBenchmark.class.getSimpleName())
//                .include(UnanticipatedTransferVariableBenchmark.class.getSimpleName())
//                .include(ByteBuddyRedefinitionBenchmark.class.getSimpleName())
//                .include(JavassistRedefinitionBenchmark.class.getSimpleName())
//                .include(WithoutConsistencyInvocationBenchmark.class.getSimpleName())
//                .include(WithConsistencyInvocationBenchmark.class.getSimpleName())
//                .include(ConsistencyBlockBenchmark.class.getSimpleName())
//                .include(LoweringBenchmark.class.getSimpleName())
//                .include(LoweringWithDelegationBenchmark.class.getSimpleName())
//                .include(LyRTAdaptationBenchmark.class.getSimpleName())
//                .include(JavassistAdaptationBenchmark.class.getSimpleName())
//                .include(KryoSerializationBenchmark.class.getSimpleName())

//                .include(ArrayDequeBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(15)
                .measurementIterations(15)
//                .jvmArgs("-server")
                .jvmArgs("-server", "-d64", "-Xms1024m", "-Xmx4048m")
//                .jvmArgs("-server", "-d64", "-Xms4048m", "-Xmx8192m")
//                .threads(1)
//                .shouldDoGC(false)
//                .shouldDoGC(true)
                .build()
        ).run();
    }
}
