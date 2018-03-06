#!/bin/sh

java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C100   -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C100   -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C100   -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C1000  -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C1000  -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C1000  -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C10000 -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C10000 -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.MemoryBaseline -C10000 -R100000

java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C100   -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C100   -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C100   -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C1000  -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C1000  -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C1000  -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C10000 -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C10000 -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.LyRTFullStack -C10000 -R100000

java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C100   -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C100   -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C100   -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C1000  -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C1000  -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C1000  -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C10000 -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C10000 -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.CachedMethodTable -C10000 -R100000


java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C100   -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C100   -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C100   -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C1000  -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C1000  -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C1000  -R100000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C10000 -R10000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C10000 -R50000
java -server -d64 -Xms4048m -Xmx8096m -cp target/benchmarks.jar memory.JindyCallable -C10000 -R100000
