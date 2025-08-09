package jmh;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.registry.Registries;
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
public class FindRecipeTest {

    @Setup
    public void setup() {
        Registries.POTION.init();
        Registries.BLOCK.init();
        Registries.ITEM.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.RECIPE.init();
    }

    @Benchmark
    public void test_findBlastFurnaceRecipe_fail(Blackhole hole) {//46
        hole.consume(Registries.RECIPE.findBlastFurnaceRecipe(Item.get(ItemID.IRON_NUGGET)));
    }

    @Benchmark
    public void test_findBlastFurnaceRecipe_success(Blackhole hole) {//46
        hole.consume(Registries.RECIPE.findBlastFurnaceRecipe(Item.get(ItemID.IRON_PICKAXE)));
    }

    @Benchmark
    public void test_findShapelessRecipe(Blackhole hole) {//1135
        hole.consume(Registries.RECIPE.findShapelessRecipe(Item.get(BlockID.BLUE_SHULKER_BOX), Item.get(ItemID.BROWN_DYE)));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FindRecipeTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
