package jmh;

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
 * Models the entity collision scan. A block position resolves to a state cheaply, but turning that
 * state into a Block goes through the registry's reflective constructor and allocates. Almost every
 * position inside an entity's bounding box is air, and Block.getBoundingBox defaults to the full
 * cube - which BlockAir does not override - so the air blocks were built, added to the collision
 * list, and then had every callback do nothing.
 * <p>
 * {@code airFraction} is the share of scanned positions that are air. An item lying on the ground
 * sees roughly 0.8-0.9.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class CollisionScanBenchmark {

    /** Stand-in for BlockState: an interned identity that says nothing about allocation. */
    private record State(int hash) {
    }

    /** Stand-in for Block: allocated per lookup, as the registry does. */
    static final class ModelBlock {
        final int hash;
        final int x;
        final int y;
        final int z;

        ModelBlock(int hash, int x, int y, int z) {
            this.hash = hash;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        boolean canPassThrough() {
            return hash == 0;
        }
    }

    private static final State AIR = new State(0);
    private static final State STONE = new State(1);

    @Param({"0.6", "0.9"})
    public double airFraction;

    /** One player-sized hitbox after the bound fix: 2 x 3 x 2 positions. */
    private static final int SPAN_X = 2;
    private static final int SPAN_Y = 3;
    private static final int SPAN_Z = 2;

    private State[] world;

    @Setup
    public void setup() {
        Random random = new Random(31337L);
        world = new State[SPAN_X * SPAN_Y * SPAN_Z];
        for (int i = 0; i < world.length; i++) {
            world[i] = random.nextDouble() < airFraction ? AIR : STONE;
        }
    }

    private State stateAt(int index) {
        return world[index];
    }

    private ModelBlock blockAt(int index, int x, int y, int z) {
        return new ModelBlock(world[index].hash(), x, y, z);
    }

    @Benchmark
    public List<ModelBlock> materialiseEveryPosition() {
        List<ModelBlock> collides = new ArrayList<>();
        int index = 0;
        for (int z = 0; z < SPAN_Z; z++) {
            for (int x = 0; x < SPAN_X; x++) {
                for (int y = 0; y < SPAN_Y; y++) {
                    ModelBlock block = blockAt(index++, x, y, z);
                    if (!block.canPassThrough()) {
                        collides.add(block);
                    }
                }
            }
        }
        return collides;
    }

    @Benchmark
    public List<ModelBlock> skipAirByState() {
        List<ModelBlock> collides = new ArrayList<>();
        int index = 0;
        for (int z = 0; z < SPAN_Z; z++) {
            for (int x = 0; x < SPAN_X; x++) {
                for (int y = 0; y < SPAN_Y; y++) {
                    int current = index++;
                    if (stateAt(current) == AIR) continue;
                    ModelBlock block = blockAt(current, x, y, z);
                    if (!block.canPassThrough()) {
                        collides.add(block);
                    }
                }
            }
        }
        return collides;
    }

    /** Guards against the model block being scalar-replaced instead of really allocated. */
    @Benchmark
    public void materialiseEveryPositionEscaping(Blackhole hole) {
        int index = 0;
        for (int z = 0; z < SPAN_Z; z++) {
            for (int x = 0; x < SPAN_X; x++) {
                for (int y = 0; y < SPAN_Y; y++) {
                    hole.consume(blockAt(index++, x, y, z));
                }
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CollisionScanBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
