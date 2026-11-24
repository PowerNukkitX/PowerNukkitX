package jmh;

import org.openjdk.jmh.annotations.*;
import org.powernukkitx.registry.Registries;

import java.util.concurrent.TimeUnit;

/**
 * Cold-start cost of each registry init, as paid once per server boot.
 * <p>
 * Every measurement runs in a fresh JVM (single shot, no warmup) so class loading and resource
 * parsing are included, like on a real startup. Prerequisites a registry needs are initialised in
 * the state's setup and are not part of the measured time.
 * <p>
 * Run with {@code ./gradlew jmh -Pbenchmark=RegistryStartupBenchmark}.
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(5)
public class RegistryStartupBenchmark {

    @State(Scope.Benchmark)
    public static class Empty {
    }

    @State(Scope.Benchmark)
    public static class BlockLoaded {
        @Setup(Level.Trial)
        public void setup() {
            Registries.BLOCK.init();
        }
    }

    @State(Scope.Benchmark)
    public static class ItemPrerequisites {
        @Setup(Level.Trial)
        public void setup() {
            Registries.POTION.init();
            Registries.EFFECT.init();
        }
    }

    @State(Scope.Benchmark)
    public static class CreativePrerequisites {
        @Setup(Level.Trial)
        public void setup() {
            Registries.BLOCK.init();
            Registries.BLOCKSTATE.init();
            Registries.POTION.init();
            Registries.EFFECT.init();
            Registries.ITEM.init();
            Registries.ENTITY.init();
            Registries.ITEM_RUNTIMEID.init();
        }
    }

    @State(Scope.Benchmark)
    public static class RecipePrerequisites extends CreativePrerequisites {
        @Override
        @Setup(Level.Trial)
        public void setup() {
            super.setup();
            Registries.CREATIVE.init();
        }
    }

    @Benchmark
    public void block(Empty s) {
        Registries.BLOCK.init();
    }

    @Benchmark
    public void blockState(BlockLoaded s) {
        Registries.BLOCKSTATE.init();
    }

    @Benchmark
    public void structure(BlockLoaded s) {
        Registries.STRUCTURE.init();
    }

    @Benchmark
    public void item(ItemPrerequisites s) {
        Registries.ITEM.init();
    }

    @Benchmark
    public void itemRuntimeId(Empty s) {
        Registries.ITEM_RUNTIMEID.init();
    }

    @Benchmark
    public void biome(Empty s) {
        Registries.BIOME.init();
    }

    @Benchmark
    public void entity(Empty s) {
        Registries.ENTITY.init();
    }

    @Benchmark
    public void blockEntity(Empty s) {
        Registries.BLOCKENTITY.init();
    }

    @Benchmark
    public void voxelShape(Empty s) {
        Registries.VOXEL_SHAPE.init();
    }

    @Benchmark
    public void creative(CreativePrerequisites s) {
        Registries.CREATIVE.init();
    }

    @Benchmark
    public void recipe(RecipePrerequisites s) {
        Registries.RECIPE.init();
    }
}
