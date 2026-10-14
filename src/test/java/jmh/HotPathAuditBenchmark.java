package jmh;

import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.utils.collection.nb.Long2ObjectNonBlockingMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Measures the specific alternatives raised by the performance audit, so the decision to change a
 * hot path is backed by a number rather than a guess. Each pair of benchmarks is the current shape
 * followed by the proposed one.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class HotPathAuditBenchmark {

    private static final int LOOKUPS = 1024;

    // --- BlockProperties.specialValueMap: declared Map<Short,V> vs the concrete primitive map ---
    private Map<Short, Object> boxedShortMap;
    private Short2ObjectOpenHashMap<Object> primitiveShortMap;
    private short[] shortKeys;

    // --- Level.tickCachedBlocks: ConcurrentHashMap<Long,V> vs Long2ObjectNonBlockingMap ---
    private ConcurrentHashMap<Long, Object> boxedLongMap;
    private Long2ObjectNonBlockingMap<Object> primitiveLongMap;
    private long[] longKeys;

    // --- LevelDBProvider.lastChunk: ThreadLocal<WeakReference<T>> vs a plain volatile field ---
    private final ThreadLocal<WeakReference<Object>> threadLocalChunk = new ThreadLocal<>();
    private volatile Object volatileChunk;

    // --- Palette.isEmpty: word scan vs remembered answer ---
    private int[] paletteWords;
    private int cachedEmptyState;

    // --- WalkingPosEvaluator.isPassable: clone per probe vs offset in place ---
    private AxisAlignedBB probeBox;

    // --- LZ4Freezer.deepCompressor: high compressor vs fast compressor on light-like data ---
    private LZ4Compressor highCompressor;
    private LZ4Compressor fastCompressor;
    private byte[] nibbleArray;

    @Setup
    public void setup() {
        Random random = new Random(987654321L);

        boxedShortMap = new Short2ObjectOpenHashMap<>();
        primitiveShortMap = new Short2ObjectOpenHashMap<>();
        shortKeys = new short[LOOKUPS];
        for (int i = 0; i < 512; i++) {
            short key = (short) i;
            Object value = new Object();
            boxedShortMap.put(key, value);
            primitiveShortMap.put(key, value);
        }
        for (int i = 0; i < LOOKUPS; i++) {
            shortKeys[i] = (short) random.nextInt(512);
        }

        boxedLongMap = new ConcurrentHashMap<>();
        primitiveLongMap = new Long2ObjectNonBlockingMap<>();
        longKeys = new long[LOOKUPS];
        for (int i = 0; i < 512; i++) {
            long key = ((long) random.nextInt(4096) << 32) | random.nextInt(4096);
            Object value = new Object();
            boxedLongMap.put(key, value);
            primitiveLongMap.put(key, value);
            longKeys[i] = key;
        }
        for (int i = 512; i < LOOKUPS; i++) {
            longKeys[i] = longKeys[random.nextInt(512)];
        }

        Object chunk = new Object();
        threadLocalChunk.set(new WeakReference<>(chunk));
        volatileChunk = chunk;

        // A 16x16x16 section at 2 bits per block is 128 words; an empty one is all zero, which is
        // the case the ticker hits over and over.
        paletteWords = new int[128];
        cachedEmptyState = 1;

        probeBox = new SimpleAxisAlignedBB(10.2, 64.0, 10.2, 11.8, 66.0, 11.8);

        LZ4Factory factory = LZ4Factory.fastestInstance();
        highCompressor = factory.highCompressor();
        fastCompressor = factory.fastCompressor();
        // Light data is a nibble array: highly repetitive, mostly a single value.
        nibbleArray = new byte[2048];
        for (int i = 0; i < nibbleArray.length; i++) {
            nibbleArray[i] = (byte) (random.nextInt(16) < 14 ? 0xFF : random.nextInt(256));
        }
    }

    @Benchmark
    public void shortKeyLookupBoxed(Blackhole hole) {
        for (short key : shortKeys) {
            hole.consume(boxedShortMap.get(key));
        }
    }

    @Benchmark
    public void shortKeyLookupPrimitive(Blackhole hole) {
        for (short key : shortKeys) {
            hole.consume(primitiveShortMap.get(key));
        }
    }

    @Benchmark
    public void longKeyLookupBoxed(Blackhole hole) {
        for (long key : longKeys) {
            hole.consume(boxedLongMap.get(key));
        }
    }

    @Benchmark
    public void longKeyLookupPrimitive(Blackhole hole) {
        for (long key : longKeys) {
            hole.consume(primitiveLongMap.get(key));
        }
    }

    @Benchmark
    public Object lastChunkThreadLocal() {
        WeakReference<Object> ref = threadLocalChunk.get();
        return ref == null ? null : ref.get();
    }

    @Benchmark
    public Object lastChunkVolatileField() {
        return volatileChunk;
    }

    @Benchmark
    public boolean paletteIsEmptyScan() {
        for (int word : paletteWords) {
            if (word != 0) return false;
        }
        return true;
    }

    @Benchmark
    public boolean paletteIsEmptyCached() {
        return cachedEmptyState == 1;
    }

    @Benchmark
    public void passableProbeClone(Blackhole hole) {
        double dr = 0.3;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                hole.consume(probeBox.clone().offset(i * dr, 0, j * dr));
            }
        }
    }

    @Benchmark
    public void passableProbeOffsetInPlace(Blackhole hole) {
        double dr = 0.3;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                probeBox.offset(i * dr, 0, j * dr);
                hole.consume(probeBox.getMinX());
                probeBox.offset(-i * dr, 0, -j * dr);
            }
        }
    }

    @Benchmark
    public byte[] deepFreezeHighCompressor() {
        return highCompressor.compress(nibbleArray);
    }

    @Benchmark
    public byte[] deepFreezeFastCompressor() {
        return fastCompressor.compress(nibbleArray);
    }

    @Benchmark
    public int chunkPayloadGrowFromDefault() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.ioBuffer();
        try {
            for (int i = 0; i < 8192; i++) buf.writeIntLE(i);
            return buf.readableBytes();
        } finally {
            buf.release();
        }
    }

    @Benchmark
    public int chunkPayloadPresized() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.ioBuffer(64 * 1024);
        try {
            for (int i = 0; i < 8192; i++) buf.writeIntLE(i);
            return buf.readableBytes();
        } finally {
            buf.release();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HotPathAuditBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
