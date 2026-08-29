package jmh;

import org.powernukkitx.utils.Binary;
import org.powernukkitx.utils.Hash;
import org.powernukkitx.utils.HashUtils;
import org.cloudburstmc.nbt.NbtMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class HashBenchmark {

    private final int[] xs = {0, 15, -128, 4096, -1000000, 100, 33554431, -33554432};
    private final int[] ys = {0, 63, 128, -64, 255, 319, 40, -32};
    private final int[] zs = {0, 15, 128, -4096, 999999, -100, 33554431, -33554432};

    private long[] blockHashes;
    private long[] xzHashes;
    private int[] chunkHashes;
    private NbtMap blockStateTag;
    private byte[] payload;

    @Setup
    public void setup() {
        blockHashes = new long[xs.length];
        xzHashes = new long[xs.length];
        chunkHashes = new int[xs.length];
        for (int i = 0; i < xs.length; i++) {
            blockHashes[i] = Hash.hashBlock(xs[i], ys[i], zs[i]);
            xzHashes[i] = HashUtils.hashXZ(xs[i], zs[i]);
            chunkHashes[i] = HashUtils.hashChunkXYZ(xs[i], ys[i], zs[i]);
        }

        final NbtMap state = NbtMap.builder()
                .putBoolean("button_pressed_bit", false)
                .putInt("facing_direction", 5)
                .build();
        blockStateTag = NbtMap.builder()
                .putString("name", "minecraft:warped_button")
                .putCompound("states", NbtMap.fromMap(new TreeMap<>(state)))
                .build();

        payload = new byte[256];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i * 31 + 7);
        }
    }

    @Benchmark
    public void hashBlock(Blackhole hole) {
        for (int i = 0; i < xs.length; i++) {
            hole.consume(Hash.hashBlock(xs[i], ys[i], zs[i]));
        }
    }

    @Benchmark
    public void hashBlockX(Blackhole hole) {
        for (long h : blockHashes) {
            hole.consume(Hash.hashBlockX(h));
        }
    }

    @Benchmark
    public void hashBlockY(Blackhole hole) {
        for (long h : blockHashes) {
            hole.consume(Hash.hashBlockY(h));
        }
    }

    @Benchmark
    public void hashBlockZ(Blackhole hole) {
        for (long h : blockHashes) {
            hole.consume(Hash.hashBlockZ(h));
        }
    }

    @Benchmark
    public void hashXZ(Blackhole hole) {
        for (int i = 0; i < xs.length; i++) {
            hole.consume(HashUtils.hashXZ(xs[i], zs[i]));
        }
    }

    @Benchmark
    public void getXFromHashXZ(Blackhole hole) {
        for (long h : xzHashes) {
            hole.consume(HashUtils.getXFromHashXZ(h));
        }
    }

    @Benchmark
    public void getZFromHashXZ(Blackhole hole) {
        for (long h : xzHashes) {
            hole.consume(HashUtils.getZFromHashXZ(h));
        }
    }

    @Benchmark
    public void hashChunkXYZ(Blackhole hole) {
        for (int i = 0; i < xs.length; i++) {
            hole.consume(HashUtils.hashChunkXYZ(xs[i], ys[i], zs[i]));
        }
    }

    @Benchmark
    public void getXFromHashChunkXYZ(Blackhole hole) {
        for (int h : chunkHashes) {
            hole.consume(HashUtils.getXFromHashChunkXYZ(h));
        }
    }

    @Benchmark
    public void getYFromHashChunkXYZ(Blackhole hole) {
        for (int h : chunkHashes) {
            hole.consume(HashUtils.getYFromHashChunkXYZ(h));
        }
    }

    @Benchmark
    public void getZFromHashChunkXYZ(Blackhole hole) {
        for (int h : chunkHashes) {
            hole.consume(HashUtils.getZFromHashChunkXYZ(h));
        }
    }

    @Benchmark
    public void fnv1a_32(Blackhole hole) {
        hole.consume(HashUtils.fnv1a_32(payload));
    }

    @Benchmark
    public void fnv164(Blackhole hole) {
        hole.consume(HashUtils.fnv164(payload));
    }

    @Benchmark
    public void fnv1a_32_nbt(Blackhole hole) {
        hole.consume(HashUtils.fnv1a_32_nbt(blockStateTag));
    }

    @Benchmark
    public void fnv1a_32_nbt_palette(Blackhole hole) {
        hole.consume(HashUtils.fnv1a_32_nbt_palette(blockStateTag));
    }

    @Benchmark
    public void bytesToHexString(Blackhole hole) {
        hole.consume(Binary.bytesToHexString(payload));
    }

    @Benchmark
    public void bytesToHexStringWithSpaces(Blackhole hole) {
        hole.consume(Binary.bytesToHexString(payload, true));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
