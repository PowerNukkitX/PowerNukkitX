package cn.powernukkitx.block;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 0)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ComputeBlockStateHashTest {
    Block block1;
    Block block2;
    Block block3;
    Block block4;
    Block block5;
    Block block6;

    @Setup
    public void setup() {
        Block.init();
        Enchantment.init();
        RuntimeItems.init();
        Potion.init();
        Item.init();
        EnumBiome.values(); // load class, this also registers biomes
        Effect.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0); // Force it to load

        block1 = BlockState.of("minecraft:acacia_fence").getBlock();
        block2 = BlockState.of("minecraft:birch_pressure_plate;redstone_signal=2")
                .getBlock();
        block3 = BlockState.of("minecraft:acacia_button;button_pressed_bit=0;facing_direction=1")
                .getBlock();
        block4 = BlockState.of("minecraft:birch_trapdoor;open_bit=0;upside_down_bit=1;direction=2")
                .getBlock();
        block5 = BlockState.of("minecraft:acacia_door;open_bit=0;upper_block_bit=0;door_hinge_bit=1;direction=1")
                .getBlock();
        block6 = BlockState.of(
                        "minecraft:cobblestone_wall;wall_connection_type_east=tall;wall_post_bit=1;wall_connection_type_south=none;wall_connection_type_west=short;wall_connection_type_north=short;wall_block_type=cobblestone")
                .getBlock();
    }

    @Benchmark
    public void test1() {
        block1.computeUnsignedBlockStateHash();
    }

    @Benchmark
    public void test2() {
        block2.computeUnsignedBlockStateHash();
    }

    @Benchmark
    public void test3() {
        block3.computeUnsignedBlockStateHash();
    }

    @Benchmark
    public void test4() {
        block4.computeUnsignedBlockStateHash();
    }

    @Benchmark
    public void test5() {
        block5.computeUnsignedBlockStateHash();
    }

    @Benchmark
    public void test6() {
        block6.computeUnsignedBlockStateHash();
    }
}
