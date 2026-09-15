package jmh;

import org.powernukkitx.math.BlockFace;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Covers the redstone propagation shapes: the face-array clone every path pays, and the pattern of
 * building a {@code Block} to ask a question the {@code BlockState} already answers.
 * <p>
 * The stand-in block is deliberately allocated behind a non-inlinable boundary so escape analysis
 * cannot delete it, because the real one comes out of the registry's reflective constructor and can
 * never be scalar-replaced.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class RedstonePropagationBenchmark {

    private record State(String identifier) {
    }

    static final class ModelBlock {
        final String identifier;
        final int x;
        final int y;
        final int z;

        ModelBlock(String identifier, int x, int y, int z) {
            this.identifier = identifier;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /** Stand-in for Vector3, allocated by the getSide(face) shape. */
    record ModelVector(int x, int y, int z) {
    }

    private static final String AIR = "minecraft:air";
    private static final String OBSERVER = "minecraft:observer";
    private static final String WIRE = "minecraft:redstone_wire";

    private static final State AIR_STATE = new State(AIR);

    /** Share of layer-1 positions that are air; waterlogging is rare, so this is close to 1. */
    @Param({"1.0", "0.9"})
    public double layer1AirFraction;

    private State[] layer0;
    private State[] layer1;

    @Setup
    public void setup() {
        Random random = new Random(77003L);
        layer0 = new State[6];
        layer1 = new State[6];
        for (int i = 0; i < 6; i++) {
            layer0[i] = new State(random.nextInt(10) == 0 ? OBSERVER : WIRE);
            layer1[i] = random.nextDouble() < layer1AirFraction ? AIR_STATE : new State(WIRE);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private ModelBlock buildBlock(String identifier, int x, int y, int z) {
        return new ModelBlock(identifier, x, y, z);
    }

    // -----------------------------------------------------------------
    // The face array itself
    // -----------------------------------------------------------------

    @Benchmark
    public void faceIterationValues(Blackhole hole) {
        for (BlockFace face : BlockFace.values()) {
            hole.consume(face.getIndex());
        }
    }

    @Benchmark
    public void faceIterationCachedArray(Blackhole hole) {
        for (BlockFace face : BlockFace.getValues()) {
            hole.consume(face.getIndex());
        }
    }

    // -----------------------------------------------------------------
    // updateAround: layer 1 queued unconditionally vs guarded by its state
    // -----------------------------------------------------------------

    @Benchmark
    public List<ModelBlock> updateAroundUnconditionalLayer1() {
        List<ModelBlock> queue = new ArrayList<>();
        int i = 0;
        for (BlockFace face : BlockFace.values()) {
            ModelBlock side = buildBlock(layer0[i].identifier(), face.getXOffset(), face.getYOffset(), face.getZOffset());
            queue.add(side);
            queue.add(buildBlock(layer1[i].identifier(), side.x, side.y, side.z));
            i++;
        }
        return queue;
    }

    @Benchmark
    public List<ModelBlock> updateAroundGuardedLayer1() {
        List<ModelBlock> queue = new ArrayList<>();
        int i = 0;
        for (BlockFace face : BlockFace.getValues()) {
            ModelBlock side = buildBlock(layer0[i].identifier(), face.getXOffset(), face.getYOffset(), face.getZOffset());
            queue.add(side);
            if (layer1[i] != AIR_STATE) {
                queue.add(buildBlock(layer1[i].identifier(), side.x, side.y, side.z));
            }
            i++;
        }
        return queue;
    }

    // -----------------------------------------------------------------
    // updateAroundObserver: Vector3 + Block per face to read an id
    // -----------------------------------------------------------------

    @Benchmark
    public int observerScanMaterialising() {
        int found = 0;
        int i = 0;
        for (BlockFace face : BlockFace.values()) {
            ModelVector side = new ModelVector(face.getXOffset(), face.getYOffset(), face.getZOffset());
            ModelBlock neighbour = buildBlock(layer0[i++].identifier(), side.x(), side.y(), side.z());
            if (OBSERVER.equals(neighbour.identifier)) found++;
        }
        return found;
    }

    @Benchmark
    public int observerScanStateFirst() {
        int found = 0;
        int i = 0;
        for (BlockFace face : BlockFace.getValues()) {
            if (!OBSERVER.equals(layer0[i++].identifier())) continue;
            ModelBlock neighbour = buildBlock(OBSERVER, face.getXOffset(), face.getYOffset(), face.getZOffset());
            if (neighbour != null) found++;
        }
        return found;
    }

    // -----------------------------------------------------------------
    // Wire mesh: a wire neighbour contributes nothing, but was materialised to find that out
    // -----------------------------------------------------------------

    @Benchmark
    public int indirectPowerMaterialising() {
        int power = 0;
        int i = 0;
        for (BlockFace face : BlockFace.values()) {
            ModelVector side = new ModelVector(face.getXOffset(), face.getYOffset(), face.getZOffset());
            ModelBlock neighbour = buildBlock(layer0[i++].identifier(), side.x(), side.y(), side.z());
            if (WIRE.equals(neighbour.identifier)) continue;
            power = Math.max(power, neighbour.x + neighbour.y + neighbour.z);
        }
        return power;
    }

    @Benchmark
    public int indirectPowerStateFirst() {
        int power = 0;
        int i = 0;
        for (BlockFace face : BlockFace.getValues()) {
            if (WIRE.equals(layer0[i++].identifier())) continue;
            ModelBlock neighbour = buildBlock(OBSERVER, face.getXOffset(), face.getYOffset(), face.getZOffset());
            power = Math.max(power, neighbour.x + neighbour.y + neighbour.z);
        }
        return power;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RedstonePropagationBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
