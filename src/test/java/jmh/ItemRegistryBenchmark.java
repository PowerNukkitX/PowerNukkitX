package jmh;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
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
public class ItemRegistryBenchmark {

    private Item pickaxe;
    private Item sword;
    private Item diamond;
    private Item stone;
    private Item helmet;
    private Item clonedPickaxe;

    @Setup
    public void setup() {
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();

        pickaxe = Item.get(ItemID.IRON_PICKAXE);
        sword = Item.get(ItemID.DIAMOND_SWORD);
        diamond = Item.get(ItemID.DIAMOND);
        stone = Item.get(BlockID.STONE);
        helmet = Item.get(ItemID.LEATHER_HELMET);
        clonedPickaxe = pickaxe.clone();
    }

    @Benchmark
    public void construct_ironPickaxe(Blackhole hole) {
        hole.consume(Item.get(ItemID.IRON_PICKAXE));
    }

    @Benchmark
    public void construct_diamondSword(Blackhole hole) {
        hole.consume(Item.get(ItemID.DIAMOND_SWORD));
    }

    @Benchmark
    public void construct_diamond(Blackhole hole) {
        hole.consume(Item.get(ItemID.DIAMOND));
    }

    @Benchmark
    public void construct_stoneBlock(Blackhole hole) {
        hole.consume(Item.get(BlockID.STONE));
    }

    @Benchmark
    public void construct_leatherHelmet(Blackhole hole) {
        hole.consume(Item.get(ItemID.LEATHER_HELMET));
    }

    @Benchmark
    public void construct_stick(Blackhole hole) {
        hole.consume(Item.get(ItemID.STICK));
    }

    @Benchmark
    public void construct_goldenApple(Blackhole hole) {
        hole.consume(Item.get(ItemID.GOLDEN_APPLE));
    }

    @Benchmark
    public void getters_identity(Blackhole hole) {
        hole.consume(pickaxe.getName());
        hole.consume(pickaxe.getId());
        hole.consume(pickaxe.getDamage());
        hole.consume(pickaxe.getCount());
        hole.consume(pickaxe.hasMeta());
        hole.consume(pickaxe.isNull());
        hole.consume(pickaxe.isBlock());
        hole.consume(pickaxe.getBlockId());
    }

    @Benchmark
    public void getters_durability(Blackhole hole) {
        hole.consume(pickaxe.getMaxStackSize());
        hole.consume(pickaxe.getMaxDurability());
        hole.consume(pickaxe.canTakeDamage());
        hole.consume(pickaxe.isUnbreakable());
        hole.consume(pickaxe.getTier());
    }

    @Benchmark
    public void getters_toolFlags(Blackhole hole) {
        hole.consume(pickaxe.isTool());
        hole.consume(pickaxe.isPickaxe());
        hole.consume(pickaxe.isSword());
        hole.consume(pickaxe.getAttackDamage());
        hole.consume(pickaxe.canBeActivated());
    }

    @Benchmark
    public void getters_armorFlags(Blackhole hole) {
        hole.consume(helmet.isArmor());
        hole.consume(helmet.isWearable());
        hole.consume(helmet.isHelmet());
        hole.consume(helmet.getArmorPoints());
        hole.consume(helmet.getToughness());
    }

    @Benchmark
    public void getters_enchant(Blackhole hole) {
        hole.consume(sword.hasEnchantments());
        hole.consume(sword.getEnchantments());
        hole.consume(sword.getEnchantAbility());
        hole.consume(sword.getRepairCost());
    }

    @Benchmark
    public void getters_placement(Blackhole hole) {
        hole.consume(stone.getCanPlaceOn());
        hole.consume(stone.getCanDestroy());
        hole.consume(stone.isBlock());
        hole.consume(stone.getBlockId());
    }

    @Benchmark
    public void getters_food(Blackhole hole) {
        hole.consume(diamond.isConsumable());
        hole.consume(diamond.isEdible());
        hole.consume(diamond.getNutrition());
        hole.consume(diamond.getSaturation());
        hole.consume(diamond.canAlwaysEat());
    }

    @Benchmark
    public void op_hashCode(Blackhole hole) {
        hole.consume(pickaxe.hashCode());
    }

    @Benchmark
    public void op_toString(Blackhole hole) {
        hole.consume(pickaxe.toString());
    }

    @Benchmark
    public void op_clone(Blackhole hole) {
        hole.consume(pickaxe.clone());
    }

    @Benchmark
    public void op_equals(Blackhole hole) {
        hole.consume(pickaxe.equals(clonedPickaxe));
    }

    @Benchmark
    public void registry_getPickaxe(Blackhole hole) {
        hole.consume(Registries.ITEM.get(ItemID.IRON_PICKAXE));
    }

    @Benchmark
    public void registry_getDiamond(Blackhole hole) {
        hole.consume(Registries.ITEM.get(ItemID.DIAMOND));
    }

    @Benchmark
    public void registry_getAll(Blackhole hole) {
        hole.consume(Registries.ITEM.getAll());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ItemRegistryBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
