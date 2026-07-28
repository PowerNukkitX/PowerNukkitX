package jmh;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.recipe.Input;
import org.powernukkitx.registry.Registries;
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
public class RecipeLookupBenchmark {

    private Item[] blastFail;
    private Item[] blastHit;
    private Item[] shapeless;
    private Item[] furnaceHit;
    private Item[] furnaceFail;
    private Item[] smoker;
    private Item[] campfire;
    private Item[] stonecutter;
    private Item[] cartography;
    private Item[] smithing;
    private Item[] brewing;
    private Item[] container;
    private Item[] modProcess;
    private Item[] multi;
    private Input shapedInput;

    @Setup
    public void setup() {
        Registries.POTION.init();
        Registries.BLOCK.init();
        Registries.ITEM.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.RECIPE.init();

        blastFail = new Item[]{Item.get(ItemID.IRON_NUGGET)};
        blastHit = new Item[]{Item.get(ItemID.IRON_PICKAXE)};
        shapeless = new Item[]{Item.get(BlockID.BLUE_SHULKER_BOX), Item.get(ItemID.BROWN_DYE)};
        furnaceHit = new Item[]{Item.get(ItemID.RAW_IRON)};
        furnaceFail = new Item[]{Item.get(ItemID.DIAMOND)};
        smoker = new Item[]{Item.get(ItemID.PORKCHOP)};
        campfire = new Item[]{Item.get(ItemID.BEEF)};
        stonecutter = new Item[]{Item.get(BlockID.STONE)};
        cartography = new Item[]{Item.get(ItemID.EMPTY_MAP)};
        smithing = new Item[]{Item.get(ItemID.NETHERITE_INGOT), Item.get(ItemID.DIAMOND_CHESTPLATE)};
        brewing = new Item[]{Item.get(ItemID.POTION), Item.get(ItemID.BLAZE_POWDER)};
        container = new Item[]{Item.get(ItemID.CHICKEN)};
        modProcess = new Item[]{Item.get(BlockID.STONE)};
        multi = new Item[]{Item.get(ItemID.IRON_PICKAXE)};

        Item plank = Item.get(BlockID.OAK_PLANKS);
        shapedInput = new Input(2, 2, new Item[][]{
                new Item[]{plank, plank},
                new Item[]{plank, plank}
        });
    }

    @Benchmark
    public void test_findBlastFurnaceRecipe_fail(Blackhole hole) {
        hole.consume(Registries.RECIPE.findBlastFurnaceRecipe(blastFail));
    }

    @Benchmark
    public void test_findBlastFurnaceRecipe_success(Blackhole hole) {
        hole.consume(Registries.RECIPE.findBlastFurnaceRecipe(blastHit));
    }

    @Benchmark
    public void test_findShapelessRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findShapelessRecipe(shapeless));
    }

    @Benchmark
    public void test_findShapedRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findShapedRecipe(shapedInput));
    }

    @Benchmark
    public void test_findFurnaceRecipe_success(Blackhole hole) {
        hole.consume(Registries.RECIPE.findFurnaceRecipe(furnaceHit));
    }

    @Benchmark
    public void test_findFurnaceRecipe_fail(Blackhole hole) {
        hole.consume(Registries.RECIPE.findFurnaceRecipe(furnaceFail));
    }

    @Benchmark
    public void test_findSmokerRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findSmokerRecipe(smoker));
    }

    @Benchmark
    public void test_findCampfireRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findCampfireRecipe(campfire));
    }

    @Benchmark
    public void test_findStonecutterRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findStonecutterRecipe(stonecutter));
    }

    @Benchmark
    public void test_findCartographyRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findCartographyRecipe(cartography));
    }

    @Benchmark
    public void test_findSmithingTransform(Blackhole hole) {
        hole.consume(Registries.RECIPE.findSmithingTransform(smithing));
    }

    @Benchmark
    public void test_findBrewingRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findBrewingRecipe(brewing));
    }

    @Benchmark
    public void test_findContainerRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findContainerRecipe(container));
    }

    @Benchmark
    public void test_findModProcessRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findModProcessRecipe(modProcess));
    }

    @Benchmark
    public void test_findMultiRecipe(Blackhole hole) {
        hole.consume(Registries.RECIPE.findMultiRecipe(multi));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RecipeLookupBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
