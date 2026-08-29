package jmh;

import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector2;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.VectorMath;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class VectorMathBenchmark {

    private final Vector3 a = new Vector3(12.5, 64.25, -33.75);
    private final Vector3 b = new Vector3(-4.5, 7.0, 128.5);
    private final Vector2 a2 = new Vector2(12.5, -33.75);
    private final Vector2 b2 = new Vector2(-4.5, 128.5);
    private final BlockVector3 ba = new BlockVector3(12, 64, -33);
    private final BlockVector3 bb = new BlockVector3(-4, 7, 128);

    @Benchmark
    public void vector3_add(Blackhole hole) {
        hole.consume(a.add(b));
    }

    @Benchmark
    public void vector3_subtract(Blackhole hole) {
        hole.consume(a.subtract(b));
    }

    @Benchmark
    public void vector3_multiply(Blackhole hole) {
        hole.consume(a.multiply(2.5));
    }

    @Benchmark
    public void vector3_length(Blackhole hole) {
        hole.consume(a.length());
    }

    @Benchmark
    public void vector3_distance(Blackhole hole) {
        hole.consume(a.distance(b));
    }

    @Benchmark
    public void vector3_distanceSquared(Blackhole hole) {
        hole.consume(a.distanceSquared(b));
    }

    @Benchmark
    public void vector3_normalize(Blackhole hole) {
        hole.consume(a.normalize());
    }

    @Benchmark
    public void vector3_dot(Blackhole hole) {
        hole.consume(a.dot(b));
    }

    @Benchmark
    public void vector3_cross(Blackhole hole) {
        hole.consume(a.cross(b));
    }

    @Benchmark
    public void vector3_floorRoundAbs(Blackhole hole) {
        hole.consume(a.floor());
        hole.consume(a.round());
        hole.consume(a.abs());
    }

    @Benchmark
    public void vector3_getSide(Blackhole hole) {
        hole.consume(a.getSide(BlockFace.UP, 2));
    }

    @Benchmark
    public void vector3_chunkCoords(Blackhole hole) {
        hole.consume(a.getChunkX());
        hole.consume(a.getChunkZ());
    }

    @Benchmark
    public void vector2_lengthDistance(Blackhole hole) {
        hole.consume(a2.length());
        hole.consume(a2.distance(b2));
        hole.consume(a2.dot(b2));
    }

    @Benchmark
    public void blockVector3_add(Blackhole hole) {
        hole.consume(ba.add(bb));
    }

    @Benchmark
    public void blockVector3_distance(Blackhole hole) {
        hole.consume(ba.distance(bb));
        hole.consume(ba.distanceSquared(bb));
    }

    @Benchmark
    public void blockVector3_upChunk(Blackhole hole) {
        hole.consume(ba.up());
        hole.consume(ba.getChunkX());
    }

    @Benchmark
    public void vectorMath_getDirection2D(Blackhole hole) {
        hole.consume(VectorMath.getDirection2D(1.25));
    }

    @Benchmark
    public void vectorMath_calculateAxisFace(Blackhole hole) {
        hole.consume(VectorMath.calculateAxis(a, b));
        hole.consume(VectorMath.calculateFace(a, b));
    }

    @Benchmark
    public void vectorMath_getPassByVector3(Blackhole hole) {
        hole.consume(VectorMath.getPassByVector3(a, b));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(VectorMathBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
