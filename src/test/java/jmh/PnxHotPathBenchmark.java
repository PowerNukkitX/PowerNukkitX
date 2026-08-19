package jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.stream.LongStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 5, time = 1)
@Threads(1)
@Fork(value = 2, jvmArgsAppend = {"-XX:+UseG1GC"})
public class PnxHotPathBenchmark {

    private static final int ASYNC_PREPARE_PARALLEL_THRESHOLD = 256;

    @Param({"20", "200"})
    public int playerCount;

    @Param({"8", "64", "512"})
    public int entityCount;

    private ConcurrentHashMap<Long, Object> players;
    private ConcurrentHashMap<Long, Object> entities;
    private ExecutorService asyncPool;

    static final class Dummy {
        double x, y, z;
        Dummy(long seed) { x = seed * 1.1; y = seed * 0.3; z = seed * 2.7; }
        double work() { return Math.sqrt(x * x + y * y + z * z) + Math.sin(x); }
    }

    @Setup
    public void setup() {
        players = new ConcurrentHashMap<>();
        for (long i = 0; i < playerCount; i++) players.put(i, new Dummy(i));
        entities = new ConcurrentHashMap<>();
        for (long i = 0; i < entityCount; i++) entities.put(i, new Dummy(i));
        asyncPool = Executors.newWorkStealingPool();
    }

    @TearDown
    public void tearDown() { asyncPool.shutdownNow(); }

    @Benchmark
    public void players_before(Blackhole bh) {
        for (Object p : new ArrayList<>(players.values())) {
            bh.consume(((Dummy) p).work());
        }
    }

    @Benchmark
    public void players_after(Blackhole bh) {
        for (Object p : players.values()) {
            bh.consume(((Dummy) p).work());
        }
    }

    @Benchmark
    public void entities_before(Blackhole bh) {
        CompletableFuture.runAsync(() ->
            LongStream.range(0, entityCount).parallel().forEach(id -> {
                Dummy e = (Dummy) entities.get(id);
                if (e != null) bh.consume(e.work());
            }), asyncPool).join();
    }

    @Benchmark
    public void entities_after(Blackhole bh) {
        if (entityCount >= ASYNC_PREPARE_PARALLEL_THRESHOLD) {
            CompletableFuture.runAsync(() ->
                LongStream.range(0, entityCount).parallel().forEach(id -> {
                    Dummy e = (Dummy) entities.get(id);
                    if (e != null) bh.consume(e.work());
                }), asyncPool).join();
        } else {
            for (long id = 0; id < entityCount; id++) {
                Dummy e = (Dummy) entities.get(id);
                if (e != null) bh.consume(e.work());
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PnxHotPathBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
