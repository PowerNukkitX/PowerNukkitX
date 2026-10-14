package jmh;

import org.powernukkitx.utils.collection.nb.Int2ObjectNonBlockingMap;
import org.powernukkitx.utils.collection.nb.Long2ObjectNonBlockingMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Models {@code Level.getTickCachedBlock}, which is on the path of every block read an entity makes
 * during a tick. The current shape does a boxed {@code computeIfAbsent} on the per-chunk store map
 * and then a second {@code computeIfAbsent} with a capturing supplier on the per-block store, so a
 * lookup that hits the cache still pays for two lambda captures and a {@code Long} box.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class TickCachedBlockLookupBenchmark {

    /** A player hitbox scan after the bounding-box fix: 1x2x1 positions, two layers. */
    private static final int READS_PER_TICK = 12;

    private final Object sentinelBlock = new Object();

    private ConcurrentHashMap<Long, ConcurrentHashMap<Integer, Object>> currentStores;
    private Long2ObjectNonBlockingMap<Int2ObjectNonBlockingMap<Object>> primitiveStores;

    private long[] chunkKeys;
    private int[] blockKeys;

    @Setup
    public void setup() {
        Random random = new Random(4242L);
        currentStores = new ConcurrentHashMap<>();
        primitiveStores = new Long2ObjectNonBlockingMap<>();

        chunkKeys = new long[READS_PER_TICK];
        blockKeys = new int[READS_PER_TICK];

        long chunkKey = ((long) 512 << 32) | 768;
        for (int i = 0; i < READS_PER_TICK; i++) {
            chunkKeys[i] = chunkKey;
            blockKeys[i] = random.nextInt(1 << 16);
        }

        ConcurrentHashMap<Integer, Object> boxedStore = new ConcurrentHashMap<>(32, 0.75f);
        Int2ObjectNonBlockingMap<Object> primitiveStore = new Int2ObjectNonBlockingMap<>(64);
        for (int blockKey : blockKeys) {
            boxedStore.put(blockKey, sentinelBlock);
            primitiveStore.put(blockKey, sentinelBlock);
        }
        currentStores.put(chunkKey, boxedStore);
        primitiveStores.put(chunkKey, primitiveStore);
    }

    @Benchmark
    public void currentComputeIfAbsent(Blackhole hole) {
        for (int i = 0; i < READS_PER_TICK; i++) {
            final int blockKey = blockKeys[i];
            Object block = currentStores
                    .computeIfAbsent(chunkKeys[i], key -> new ConcurrentHashMap<>(32, 0.75f))
                    .computeIfAbsent(blockKey, ignore -> sentinelBlock);
            hole.consume(block);
        }
    }

    @Benchmark
    public void primitiveGetThenPut(Blackhole hole) {
        for (int i = 0; i < READS_PER_TICK; i++) {
            Int2ObjectNonBlockingMap<Object> store = primitiveStores.get(chunkKeys[i]);
            if (store == null) {
                Int2ObjectNonBlockingMap<Object> created = new Int2ObjectNonBlockingMap<>(64);
                Int2ObjectNonBlockingMap<Object> prev = primitiveStores.putIfAbsent(chunkKeys[i], created);
                store = prev != null ? prev : created;
            }
            Object block = store.get(blockKeys[i]);
            if (block == null) {
                Object prev = store.putIfAbsent(blockKeys[i], sentinelBlock);
                block = prev != null ? prev : sentinelBlock;
            }
            hole.consume(block);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TickCachedBlockLookupBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
