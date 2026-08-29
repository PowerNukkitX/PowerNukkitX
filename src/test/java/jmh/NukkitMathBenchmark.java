package jmh;

import org.powernukkitx.math.MathHelper;
import org.powernukkitx.math.NukkitMath;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class NukkitMathBenchmark {

    private final Random random = new Random(1234567L);
    private final double sampleDouble = 1234.56789;
    private final float sampleFloat = 1234.5678f;

    @Benchmark
    public void floorDouble(Blackhole hole) {
        hole.consume(NukkitMath.floorDouble(sampleDouble));
    }

    @Benchmark
    public void ceilDouble(Blackhole hole) {
        hole.consume(NukkitMath.ceilDouble(sampleDouble));
    }

    @Benchmark
    public void floorFloat(Blackhole hole) {
        hole.consume(NukkitMath.floorFloat(sampleFloat));
    }

    @Benchmark
    public void ceilFloat(Blackhole hole) {
        hole.consume(NukkitMath.ceilFloat(sampleFloat));
    }

    @Benchmark
    public void round(Blackhole hole) {
        hole.consume(NukkitMath.round(sampleDouble));
    }

    @Benchmark
    public void roundPrecision(Blackhole hole) {
        hole.consume(NukkitMath.round(sampleDouble, 2));
    }

    @Benchmark
    public void clampDouble(Blackhole hole) {
        hole.consume(NukkitMath.clamp(sampleDouble, 0.0, 500.0));
    }

    @Benchmark
    public void clampInt(Blackhole hole) {
        hole.consume(NukkitMath.clamp(1234, 0, 500));
    }

    @Benchmark
    public void remap(Blackhole hole) {
        hole.consume(NukkitMath.remap(sampleFloat, 0f, 2000f, 0f, 10f));
    }

    @Benchmark
    public void remapNormalized(Blackhole hole) {
        hole.consume(NukkitMath.remapNormalized(sampleFloat, 0f, 2000f));
    }

    @Benchmark
    public void remapFromNormalized(Blackhole hole) {
        hole.consume(NukkitMath.remapFromNormalized(0.5f, 0f, 10f));
    }

    @Benchmark
    public void isZero(Blackhole hole) {
        hole.consume(NukkitMath.isZero(0));
    }

    @Benchmark
    public void getRandomNumberInRange(Blackhole hole) {
        hole.consume(MathHelper.getRandomNumberInRange(random, 5, 10));
    }

    @Benchmark
    public void sqrt(Blackhole hole) {
        hole.consume(MathHelper.sqrt(sampleFloat));
    }

    @Benchmark
    public void sin(Blackhole hole) {
        hole.consume(MathHelper.sin(sampleFloat));
    }

    @Benchmark
    public void cos(Blackhole hole) {
        hole.consume(MathHelper.cos(sampleFloat));
    }

    @Benchmark
    public void floorHelper(Blackhole hole) {
        hole.consume(MathHelper.floor(sampleDouble));
    }

    @Benchmark
    public void floorDoubleLong(Blackhole hole) {
        hole.consume(MathHelper.floor_double_long(sampleDouble));
    }

    @Benchmark
    public void floorFloatInt(Blackhole hole) {
        hole.consume(MathHelper.floor_float_int(sampleFloat));
    }

    @Benchmark
    public void abs(Blackhole hole) {
        hole.consume(MathHelper.abs(-1234));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NukkitMathBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
