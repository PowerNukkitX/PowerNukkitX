package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.*;
import cn.nukkit.block.property.enums.LeverDirection;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.RuledObjectGenerator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;

import java.util.Map;

import static cn.nukkit.block.property.CommonBlockProperties.ATTACHED_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.LEVER_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.REPEATER_DELAY;
import static cn.nukkit.block.property.CommonBlockProperties.VINE_DIRECTION_BITS;
import static cn.nukkit.block.property.CommonBlockProperties.WEIRDO_DIRECTION;

public class ObjectJungleTemple extends RuledObjectGenerator {

    protected static final int MIN_DISTANCE = 8;
    protected static final int MAX_DISTANCE = 32;

    protected static final BlockState COBBLESTONE = BlockCobblestone.PROPERTIES.getDefaultState();
    protected static final BlockState MOSSY_STONE = BlockMossyCobblestone.PROPERTIES.getDefaultState();
    protected static final BlockState TRIP_WIRE = BlockTripWire.PROPERTIES.getDefaultState();
    protected static final BlockState CHISELED_STONE_BRICK = BlockChiseledStoneBricks.PROPERTIES.getDefaultState();
    protected static final BlockState AIR = BlockAir.STATE;
    protected static final BlockState STAIRS_N = BlockStoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(2));
    protected static final BlockState STAIRS_E = BlockStoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(1));
    protected static final BlockState STAIRS_S = BlockStoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(3));
    protected static final BlockState STAIRS_W = BlockStoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(0));
    protected static final BlockState DISPENSER_N = BlockDispenser.PROPERTIES.getBlockState(FACING_DIRECTION.createValue(3));
    protected static final BlockState DISPENSER_E = BlockDispenser.PROPERTIES.getBlockState(FACING_DIRECTION.createValue(4));
    protected static final BlockState TRIPWIRE_HOOK_E = BlockTripwireHook.PROPERTIES.getBlockState(DIRECTION.createValue(1), ATTACHED_BIT.createValue(true));
    protected static final BlockState TRIPWIRE_HOOK_W = BlockTripwireHook.PROPERTIES.getBlockState(DIRECTION.createValue(3), ATTACHED_BIT.createValue(true));
    protected static final BlockState TRIPWIRE_HOOK_N = BlockTripwireHook.PROPERTIES.getBlockState(DIRECTION.createValue(0), ATTACHED_BIT.createValue(true));
    protected static final BlockState TRIPWIRE_HOOK_S = BlockTripwireHook.PROPERTIES.getBlockState(DIRECTION.createValue(2), ATTACHED_BIT.createValue(true));
    protected static final BlockState REDSTONE_WIRE = BlockRedstoneWire.PROPERTIES.getDefaultState();
    protected static final BlockState UNPOWERED_REPEATER_1 = BlockUnpoweredRepeater.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.SOUTH), REPEATER_DELAY.createValue(1));
    protected static final BlockState STICKY_PISTON_5 = BlockStickyPiston.PROPERTIES.getBlockState(FACING_DIRECTION.createValue(5));
    protected static final BlockState STICKY_PISTON_1 = BlockStickyPiston.PROPERTIES.getBlockState(FACING_DIRECTION.createValue(1));

    protected static final BlockState LEVER_S = BlockLever.PROPERTIES.getBlockState(LEVER_DIRECTION.createValue(LeverDirection.SOUTH));
    protected static final BlockState VINE_4 = BlockVine.PROPERTIES.getBlockState(VINE_DIRECTION_BITS.createValue(4));
    protected static final BlockState VINE_8 = BlockVine.PROPERTIES.getBlockState(VINE_DIRECTION_BITS.createValue(8));

    protected static final BlockState CHEST_W = BlockChest.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.WEST));
    protected static final BlockState CHEST_N = BlockChest.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.NORTH));

    protected static final ChestPopulator CHEST_POPULATOR = new ChestPopulator();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY()-3;
        int z = position.getFloorZ();
        StructureHelper builder = new StructureHelper(level.getLevel(), new BlockVector3(x, y, z));
        Map<BlockState, Integer> stones = new Object2IntArrayMap<>();
        builder.addRandomBlock(stones, 4, COBBLESTONE);
        builder.addRandomBlock(stones, 6, MOSSY_STONE);

        // 1st floor
        builder.fillWithRandomBlock(new BlockVector3(0, 0, 0), new BlockVector3(11, 0, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 1, 0), new BlockVector3(11, 3, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(11, 1, 1), new BlockVector3(11, 3, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 1, 1), new BlockVector3(0, 3, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 1, 14), new BlockVector3(11, 3, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(0, 4, 0), new BlockVector3(11, 4, 14), random, stones);
        builder.fill(new BlockVector3(4, 4, 0), new BlockVector3(7, 4, 0), STAIRS_N); // N
        builder.fill(new BlockVector3(1, 1, 1), new BlockVector3(10, 3, 13), AIR);
        builder.fill(new BlockVector3(5, 4, 7), new BlockVector3(6, 4, 9), AIR);

        // 2nd floor
        builder.fillWithRandomBlock(new BlockVector3(2, 5, 2), new BlockVector3(9, 6, 2), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 5, 3), new BlockVector3(9, 6, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 5, 12), new BlockVector3(9, 6, 12), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 5, 3), new BlockVector3(2, 6, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 7, 1), new BlockVector3(10, 7, 13), random, stones);
        builder.fill(new BlockVector3(3, 5, 3), new BlockVector3(8, 6, 11), AIR);
        builder.fill(new BlockVector3(4, 7, 6), new BlockVector3(7, 7, 9), AIR);
        builder.fill(new BlockVector3(5, 5, 2), new BlockVector3(6, 6, 2), AIR);
        builder.fill(new BlockVector3(5, 6, 12), new BlockVector3(6, 6, 12), AIR);

        // 3rd floor
        builder.fillWithRandomBlock(new BlockVector3(1, 8, 1), new BlockVector3(10, 9, 1), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(10, 8, 2), new BlockVector3(10, 9, 12), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 8, 13), new BlockVector3(10, 9, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 8, 2), new BlockVector3(1, 9, 12), random, stones);
        builder.fill(new BlockVector3(2, 8, 2), new BlockVector3(9, 9, 12), AIR);
        builder.fill(new BlockVector3(5, 9, 1), new BlockVector3(6, 9, 1), AIR);
        builder.fill(new BlockVector3(5, 9, 13), new BlockVector3(6, 9, 13), AIR);
        builder.setBlockStateAt(new BlockVector3(10, 9, 5), AIR);
        builder.setBlockStateAt(new BlockVector3(10, 9, 9), AIR);
        builder.setBlockStateAt(new BlockVector3(1, 9, 5), AIR);
        builder.setBlockStateAt(new BlockVector3(1, 9, 9), AIR);

        // roof
        builder.fillWithRandomBlock(new BlockVector3(1, 10, 1), new BlockVector3(10, 10, 4), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 10, 5), new BlockVector3(10, 10, 9), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 10, 5), new BlockVector3(3, 10, 9), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(1, 10, 10), new BlockVector3(10, 10, 13), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(3, 11, 3), new BlockVector3(8, 11, 5), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 11, 6), new BlockVector3(8, 11, 8), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(3, 11, 6), new BlockVector3(4, 11, 8), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(3, 11, 9), new BlockVector3(8, 11, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 12, 4), new BlockVector3(7, 12, 10), random, stones);
        builder.fill(new BlockVector3(4, 10, 5), new BlockVector3(7, 10, 9), AIR);
        builder.fill(new BlockVector3(5, 11, 6), new BlockVector3(6, 11, 8), AIR);

        // outside walls decorations
        builder.fillWithRandomBlock(new BlockVector3(2, 8, 0), new BlockVector3(2, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 8, 0), new BlockVector3(4, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 8, 0), new BlockVector3(7, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 8, 0), new BlockVector3(9, 9, 0), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 10, 0), new BlockVector3(6, 10, 0), random, stones);
        for (int i = 0; i < 6; i++) {
            builder.fillWithRandomBlock(new BlockVector3(11, 8, 2 + (i << 1)), new BlockVector3(11, 9, 2 + (i << 1)), random, stones);
            builder.fillWithRandomBlock(new BlockVector3(0, 8, 2 + (i << 1)), new BlockVector3(0, 9, 2 + (i << 1)), random, stones);
        }
        builder.setBlockWithRandomBlock(new BlockVector3(11, 10, 5), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(11, 10, 9), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(0, 10, 5), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(0, 10, 9), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 8, 14), new BlockVector3(2, 9, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 8, 14), new BlockVector3(4, 9, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 8, 14), new BlockVector3(7, 9, 14), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 8, 14), new BlockVector3(9, 9, 14), random, stones);

        // roof decorations
        builder.fillWithRandomBlock(new BlockVector3(2, 11, 2), new BlockVector3(2, 13, 2), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 11, 2), new BlockVector3(9, 13, 2), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 11, 12), new BlockVector3(9, 13, 12), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(2, 11, 12), new BlockVector3(2, 13, 12), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(4, 13, 4), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(7, 13, 4), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(7, 13, 10), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(4, 13, 10), random, stones);
        builder.fill(new BlockVector3(5, 13, 6), new BlockVector3(6, 13, 6), STAIRS_N); // N
        builder.fillWithRandomBlock(new BlockVector3(5, 13, 7), new BlockVector3(6, 13, 7), random, stones);
        builder.fill(new BlockVector3(5, 13, 8), new BlockVector3(6, 13, 8), STAIRS_S); // S

        // 1st floor inside
        for (int i = 0; i < 6; i++) {
            builder.fillWithRandomBlock(new BlockVector3(1, 3, 2 + (i << 1)), new BlockVector3(3, 3, 2 + (i << 1)), random, stones);
        }
        for (int i = 0; i < 7; i++) {
            builder.fillWithRandomBlock(new BlockVector3(1, 1, 1 + (i << 1)), new BlockVector3(1, 2, 1 + (i << 1)), random, stones);
        }
        builder.setBlockWithRandomBlock(new BlockVector3(2, 2, 1), random, stones);
        builder.setBlockStateAt(new BlockVector3(3, 1, 1), MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(4, 2, 1), new BlockVector3(5, 2, 1), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(6, 1, 1), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(6, 3, 1), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(7, 2, 1), new BlockVector3(9, 2, 1), random, stones);
        builder.setBlockStateAt(new BlockVector3(8, 1, 1), MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(10, 1, 1), new BlockVector3(10, 3, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 3, 1), new BlockVector3(9, 3, 7), random, stones);
        builder.setBlockStateAt(new BlockVector3(9, 1, 2), MOSSY_STONE);
        builder.setBlockStateAt(new BlockVector3(9, 1, 4), MOSSY_STONE);
        builder.setBlockStateAt(new BlockVector3(8, 1, 5), MOSSY_STONE);
        builder.fill(new BlockVector3(7, 2, 5), new BlockVector3(7, 3, 5), MOSSY_STONE);
        builder.setBlockStateAt(new BlockVector3(6, 1, 5), MOSSY_STONE);
        builder.setBlockWithRandomBlock(new BlockVector3(6, 2, 5), random, stones);
        builder.fill(new BlockVector3(5, 2, 5), new BlockVector3(5, 3, 5), MOSSY_STONE);
        builder.setBlockStateAt(new BlockVector3(4, 1, 5), MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(7, 1, 6), new BlockVector3(7, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(4, 1, 6), new BlockVector3(4, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 3, 11), new BlockVector3(6, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 3, 11), new BlockVector3(10, 3, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 1, 11), new BlockVector3(10, 1, 11), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 1, 8), new BlockVector3(6, 1, 8), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(6, 1, 7), new BlockVector3(6, 2, 7), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(5, 2, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(6, 1, 6), new BlockVector3(6, 3, 6), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(5, 2, 6), new BlockVector3(5, 3, 6), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 2, 6), new BlockVector3(9, 2, 6), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(8, 3, 6), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(9, 1, 7), new BlockVector3(9, 2, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(8, 1, 7), new BlockVector3(8, 3, 7), random, stones);
        builder.fillWithRandomBlock(new BlockVector3(10, 1, 8), new BlockVector3(10, 1, 10), random, stones);
        builder.setBlockStateAt(new BlockVector3(10, 2, 9), MOSSY_STONE);
        builder.fillWithRandomBlock(new BlockVector3(8, 1, 8), new BlockVector3(8, 1, 10), random, stones);
        builder.fill(new BlockVector3(8, 2, 11), new BlockVector3(10, 2, 11), CHISELED_STONE_BRICK);
        builder.fill(new BlockVector3(8, 2, 12), new BlockVector3(10, 2, 12), LEVER_S); // N
        builder.setBlockStateAt(new BlockVector3(3, 2, 1), DISPENSER_N); // N
        //TODO: tile
        builder.setBlockStateAt(new BlockVector3(9, 2, 3), DISPENSER_E); // E
        //TODO: tile
        builder.setBlockStateAt(new BlockVector3(3, 2, 2), VINE_4); // NESW
        builder.fill(new BlockVector3(8, 2, 3), new BlockVector3(8, 3, 3), VINE_8); // NESW
        builder.fill(new BlockVector3(2, 1, 8), new BlockVector3(3, 1, 8), TRIP_WIRE);
        builder.setBlockStateAt(new BlockVector3(4, 1, 8), TRIPWIRE_HOOK_E); // E
        builder.setBlockStateAt(new BlockVector3(1, 1, 8), TRIPWIRE_HOOK_W); // W
        builder.fill(new BlockVector3(5, 1, 1), new BlockVector3(5, 1, 7), REDSTONE_WIRE);
        builder.setBlockStateAt(new BlockVector3(4, 1, 1), REDSTONE_WIRE);
        builder.fill(new BlockVector3(7, 1, 2), new BlockVector3(7, 1, 4), TRIP_WIRE);
        builder.setBlockStateAt(new BlockVector3(7, 1, 1), TRIPWIRE_HOOK_N); // N
        builder.setBlockStateAt(new BlockVector3(7, 1, 5), TRIPWIRE_HOOK_S); // S
        builder.fill(new BlockVector3(8, 1, 6), new BlockVector3(9, 1, 6), REDSTONE_WIRE);
        builder.setBlockStateAt(new BlockVector3(9, 1, 5), REDSTONE_WIRE);
        builder.setBlockStateAt(new BlockVector3(9, 2, 4), REDSTONE_WIRE);
        builder.fill(new BlockVector3(10, 2, 8), new BlockVector3(10, 3, 8), STICKY_PISTON_5);
        builder.setBlockStateAt(new BlockVector3(9, 2, 8), STICKY_PISTON_1);
        builder.setBlockStateAt(new BlockVector3(10, 3, 9), REDSTONE_WIRE);
        builder.fill(new BlockVector3(8, 2, 9), new BlockVector3(8, 2, 10), REDSTONE_WIRE);
        builder.setBlockStateAt(new BlockVector3(10, 2, 10), UNPOWERED_REPEATER_1); // N
        builder.setBlockStateAt(new BlockVector3(8, 1, 3), CHEST_W); // E

        builder.setBlockStateAt(new BlockVector3(9, 1, 10), CHEST_N); // S

        // 2nd floor inside
        for (int i = 0; i < 4; i++) {
            builder.fill(new BlockVector3(5, 4 - i, 6 + i), new BlockVector3(6, 4 - i, 6 + i), STAIRS_S); // S
        }
        builder.fillWithRandomBlock(new BlockVector3(4, 5, 10), new BlockVector3(7, 6, 10), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(4, 5, 9), random, stones);
        builder.setBlockWithRandomBlock(new BlockVector3(7, 5, 9), random, stones);
        for (int i = 0; i < 3; i++) {
            builder.setBlockStateAt(new BlockVector3(7, 5 + i, 8 + i), STAIRS_N); // N
            builder.setBlockStateAt(new BlockVector3(4, 5 + i, 8 + i), STAIRS_N); // N
        }

        // 3rd floor inside
        builder.fillWithRandomBlock(new BlockVector3(5, 8, 5), new BlockVector3(6, 8, 5), random, stones);
        builder.setBlockStateAt(new BlockVector3(7, 8, 5), STAIRS_E); // E
        builder.setBlockStateAt(new BlockVector3(4, 8, 5), STAIRS_W); // W
        for(Block block : builder.getBlocks()) {
            if(block instanceof BlockChest chest) {
                CHEST_POPULATOR.create(chest.getOrCreateBlockEntity().getInventory(), random);
            }
            if(block instanceof BlockDispenser dispenser) {
                dispenser.getOrCreateBlockEntity().getInventory().addItem(Item.get(Item.ARROW, 0, rand.nextInt(2, 8)));
            }
            if(block instanceof BlockStickyPiston piston) {
                var nbt = BlockEntity.getDefaultCompound(piston, BlockEntity.PISTON_ARM)
                        .putInt("facing", piston.getBlockFace().getIndex())
                        .putBoolean("Sticky", piston.sticky)
                        .putBoolean("powered", piston.isGettingPower());
                piston.createBlockEntity(nbt);
            }
        }

        level.merge(builder);
        return true;
    }

    @Override
    public String getName() {
        return "jungle_temple";
    }

    @Override
    public boolean canGenerateAt(Location location) {
        int x = location.getFloorX();
        int y = location.getFloorY();
        int z = location.getFloorZ();
        int chunkX = location.getChunkX();
        int chunkZ = location.getChunkZ();
        Level level = location.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));

        int biome = level.getBiomeId(x, y, z);
        return biome == BiomeID.JUNGLE && ((chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX
                && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ);
    }

    protected static class ChestPopulator extends RandomizableContainer {
        public ChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.DIAMOND, 1, 3, 15))
                    .register(new ItemEntry(Item.IRON_INGOT, 1, 5, 50))
                    .register(new ItemEntry(Item.GOLD_INGOT, 2, 7, 75))
                    .register(new ItemEntry(Item.EMERALD, 1, 3, 10))
                    .register(new ItemEntry(Item.BONE, 4, 6, 100))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 3, 7, 80))
                    .register(new ItemEntry(Block.BAMBOO, 1, 3, 75))
                    .register(new ItemEntry(Item.LEATHER, 1, 5, 15))
                    .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 6, getDefaultEnchantments()));
            this.pools.put(pool1.build(), new RollEntry(6, 2, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Block.AIR, 2))
                    .register(new ItemEntry(Item.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, 2, 2, 1));
            this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
        }
    }
}
