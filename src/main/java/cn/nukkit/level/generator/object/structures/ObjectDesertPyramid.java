package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.*;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.block.property.enums.MinecraftVerticalHalf;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.RuledObjectGenerator;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_VERTICAL_HALF;
import static cn.nukkit.block.property.CommonBlockProperties.WEIRDO_DIRECTION;

public class ObjectDesertPyramid extends RuledObjectGenerator {

    protected static final BlockState SANDSTONE = BlockSandstone.PROPERTIES.getDefaultState();
    protected static final BlockState CUT_SANDSTONE = BlockCutSandstone.PROPERTIES.getDefaultState();
    protected static final BlockState CHISELED_SANDSTONE = BlockChiseledSandstone.PROPERTIES.getDefaultState();
    protected static final BlockState SLAB = BlockSandstoneSlab.PROPERTIES.getDefaultState();
    protected static final BlockState SLAB_UP = BlockSandstoneSlab.PROPERTIES.getBlockState(MINECRAFT_VERTICAL_HALF.createValue(MinecraftVerticalHalf.TOP));
    protected static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    protected static final BlockState AIR = BlockAir.STATE;
    protected static final BlockState TNT = BlockTnt.PROPERTIES.getDefaultState();
    protected static final BlockState STONE_PRESSURE_PLATE = BlockStonePressurePlate.PROPERTIES.getDefaultState();
    protected static final BlockState STAIRS_N = BlockSandstoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(2));
    protected static final BlockState STAIRS_E = BlockSandstoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(1));
    protected static final BlockState STAIRS_S = BlockSandstoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(3));
    protected static final BlockState STAIRS_W = BlockSandstoneStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(0));
    protected static final BlockState TERRACOTTA_ORANGE = BlockOrangeTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState TERRACOTTA_BLUE = BlockBlueTerracotta.PROPERTIES.getDefaultState();
    protected static final BlockState CHEST_N = BlockChest.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.NORTH));
    protected static final BlockState CHEST_E = BlockChest.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.EAST));
    protected static final BlockState CHEST_S = BlockChest.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.SOUTH));
    protected static final BlockState CHEST_W = BlockChest.PROPERTIES.getBlockState(MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.WEST));

    protected static final ChestPopulator CHEST_POPULATOR = new ChestPopulator();

    @Override
    public boolean generate(BlockManager manager, RandomSourceProvider rand1, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY() - 20;
        int z = position.getFloorZ();
        StructureHelper builder = new StructureHelper(manager.getLevel(), new BlockVector3(x, y, z));
        for (int x2 = 0; x2 < 21; x2++) {
            for (int z2 = 0; z2 < 21; z2++) {
                AxisAlignedBB b = this.getBoundingBox();
                BlockVector3 vec =  new BlockVector3((int) b.getMinX(), (int) b.getMinY(), (int) b.getMinZ()).add(new BlockVector3(x2, 13, z2));
                int y2 = vec.y;
                while (!builder.getBlockAt(vec.x, y2, vec.z).isSolid() && y2 > 1) {
                    builder.setBlockStateAt(vec.x, y2, vec.z, SANDSTONE);
                    y2--;
                }
            }
        }
        builder.fill(new BlockVector3(0, 14, 0), new BlockVector3(20, 18, 20), SANDSTONE);
        for (int i = 1; i <= 9; i++) {
            builder.fill(new BlockVector3(i, i + 18, i), new BlockVector3(20 - i, i + 18, 20 - i), SANDSTONE);
            builder.fill(new BlockVector3(i + 1, i + 18, i + 1), new BlockVector3(19 - i, i + 18, 19 - i), AIR);
        }
        // east tower
        builder.fill(new BlockVector3(0, 18, 0), new BlockVector3(4, 27, 4), SANDSTONE, AIR);
        builder.fill(new BlockVector3(1, 28, 1), new BlockVector3(3, 28, 3), SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(2, 28, 0), STAIRS_N); // N
        builder.setBlockStateAt(new BlockVector3(4, 28, 2), STAIRS_E); // E
        builder.setBlockStateAt(new BlockVector3(2, 28, 4), STAIRS_S); // S
        builder.setBlockStateAt(new BlockVector3(0, 28, 2), STAIRS_W); // W
        builder.fill(new BlockVector3(1, 19, 5), new BlockVector3(3, 22, 11), SANDSTONE);
        builder.fill(new BlockVector3(2, 22, 4), new BlockVector3(2, 24, 4), AIR);
        builder.fill(new BlockVector3(1, 19, 3), new BlockVector3(2, 20, 3), SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(1, 19, 2), SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(1, 20, 2), SLAB);
        builder.setBlockStateAt(new BlockVector3(2, 19, 2), STAIRS_E); // E
        for (int i = 0; i < 2; i++) {
            builder.setBlockStateAt(new BlockVector3(2, 21 + i, 4 + i), STAIRS_N); // N
        }
        // west tower
        builder.fill(new BlockVector3(16, 18, 0), new BlockVector3(20, 27, 4), SANDSTONE, AIR);
        builder.fill(new BlockVector3(17, 28, 1), new BlockVector3(19, 28, 3), SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(18, 28, 0), STAIRS_N); // N
        builder.setBlockStateAt(new BlockVector3(20, 28, 2), STAIRS_E); // E
        builder.setBlockStateAt(new BlockVector3(18, 28, 4), STAIRS_S); // S
        builder.setBlockStateAt(new BlockVector3(16, 28, 2), STAIRS_W); // W
        builder.fill(new BlockVector3(17, 19, 5), new BlockVector3(19, 22, 11), SANDSTONE);
        builder.fill(new BlockVector3(18, 22, 4), new BlockVector3(18, 24, 4), AIR);
        builder.fill(new BlockVector3(18, 19, 3), new BlockVector3(19, 20, 3), SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(19, 19, 2), SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(19, 20, 2), SLAB_UP);
        builder.setBlockStateAt(new BlockVector3(18, 19, 2), STAIRS_W); // W
        for (int i = 0; i < 2; i++) {
            builder.setBlockStateAt(new BlockVector3(18, 21 + i, 4 + i), STAIRS_N); // N
        }
        // tower symbols
        for (int i = 0; i < 2; i++) {
            // front
            builder.fill(new BlockVector3(1 + (i << 4), 20, 0), new BlockVector3(1 + (i << 4), 21, 0), CUT_SANDSTONE);
            builder.fill(new BlockVector3(2 + (i << 4), 20, 0), new BlockVector3(2 + (i << 4), 21, 0), TERRACOTTA_ORANGE);
            builder.fill(new BlockVector3(3 + (i << 4), 20, 0), new BlockVector3(3 + (i << 4), 21, 0), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(1 + (i << 4), 22, 0), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(2 + (i << 4), 22, 0), CHISELED_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(3 + (i << 4), 22, 0), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(1 + (i << 4), 23, 0), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(2 + (i << 4), 23, 0), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(3 + (i << 4), 23, 0), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(1 + (i << 4), 24, 0), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(2 + (i << 4), 24, 0), CHISELED_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(3 + (i << 4), 24, 0), TERRACOTTA_ORANGE);
            builder.fill(new BlockVector3(1 + (i << 4), 25, 0), new BlockVector3(3 + (i << 4), 25, 0), TERRACOTTA_ORANGE);
            builder.fill(new BlockVector3(1 + (i << 4), 26, 0), new BlockVector3(3 + (i << 4), 26, 0), CUT_SANDSTONE);
            // side
            builder.fill(new BlockVector3(i * 20, 20, 1), new BlockVector3(i * 20, 21, 1), CUT_SANDSTONE);
            builder.fill(new BlockVector3(i * 20, 20, 2), new BlockVector3(i * 20, 21, 2), TERRACOTTA_ORANGE);
            builder.fill(new BlockVector3(i * 20, 20, 3), new BlockVector3(i * 20, 21, 3), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 22, 1), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 22, 2), CHISELED_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 22, 3), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 23, 1), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 23, 2), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 23, 3), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 24, 1), TERRACOTTA_ORANGE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 24, 2), CHISELED_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(i * 20, 24, 3), TERRACOTTA_ORANGE);
            builder.fill(new BlockVector3(i * 20, 25, 1), new BlockVector3(i * 20, 25, 3), TERRACOTTA_ORANGE);
            builder.fill(new BlockVector3(i * 20, 26, 1), new BlockVector3(i * 20, 26, 3), CUT_SANDSTONE);
        }
        // front entrance
        builder.fill(new BlockVector3(8, 18, 1), new BlockVector3(12, 22, 4), SANDSTONE, AIR);
        builder.fill(new BlockVector3(9, 19, 0), new BlockVector3(11, 21, 4), AIR);
        builder.fill(new BlockVector3(9, 19, 1), new BlockVector3(9, 20, 1), CUT_SANDSTONE);
        builder.fill(new BlockVector3(11, 19, 1), new BlockVector3(11, 20, 1), CUT_SANDSTONE);
        builder.fill(new BlockVector3(9, 21, 1), new BlockVector3(11, 21, 1), CUT_SANDSTONE);
        builder.fill(new BlockVector3(8, 18, 0), new BlockVector3(8, 21, 0), SANDSTONE);
        builder.fill(new BlockVector3(12, 18, 0), new BlockVector3(12, 21, 0), SANDSTONE);
        builder.fill(new BlockVector3(8, 22, 0), new BlockVector3(12, 22, 0), CUT_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(8, 23, 0), CUT_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(9, 23, 0), TERRACOTTA_ORANGE);
        builder.setBlockStateAt(new BlockVector3(10, 23, 0), CHISELED_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(11, 23, 0), TERRACOTTA_ORANGE);
        builder.setBlockStateAt(new BlockVector3(12, 23, 0), CUT_SANDSTONE);
        builder.fill(new BlockVector3(9, 24, 0), new BlockVector3(11, 24, 0), CUT_SANDSTONE);
        // east entrance
        builder.fill(new BlockVector3(5, 23, 9), new BlockVector3(5, 25, 11), CUT_SANDSTONE);
        builder.fill(new BlockVector3(6, 25, 9), new BlockVector3(6, 25, 11), SANDSTONE);
        builder.fill(new BlockVector3(5, 23, 10), new BlockVector3(6, 24, 10), AIR);
        // west entrance
        builder.fill(new BlockVector3(15, 23, 9), new BlockVector3(15, 25, 11), CUT_SANDSTONE);
        builder.fill(new BlockVector3(14, 25, 9), new BlockVector3(14, 25, 11), SANDSTONE);
        builder.fill(new BlockVector3(14, 23, 10), new BlockVector3(15, 24, 10), AIR);
        // corridor to east tower
        builder.fill(new BlockVector3(4, 19, 1), new BlockVector3(8, 21, 3), SANDSTONE, AIR);
        builder.fill(new BlockVector3(4, 19, 2), new BlockVector3(8, 20, 2), AIR);
        // corridor to west tower
        builder.fill(new BlockVector3(12, 19, 1), new BlockVector3(16, 21, 3), SANDSTONE, AIR);
        builder.fill(new BlockVector3(12, 19, 2), new BlockVector3(16, 20, 2), AIR);
        // pillars in the middle of 1st floor
        builder.fill(new BlockVector3(8, 19, 8), new BlockVector3(8, 21, 8), CUT_SANDSTONE);
        builder.fill(new BlockVector3(12, 19, 8), new BlockVector3(12, 21, 8), CUT_SANDSTONE);
        builder.fill(new BlockVector3(12, 19, 12), new BlockVector3(12, 21, 12), CUT_SANDSTONE);
        builder.fill(new BlockVector3(8, 19, 12), new BlockVector3(8, 21, 12), CUT_SANDSTONE);
        // 2nd floor
        builder.fill(new BlockVector3(5, 22, 5), new BlockVector3(15, 22, 15), SANDSTONE);
        builder.fill(new BlockVector3(9, 22, 9), new BlockVector3(11, 22, 11), AIR);
        // east and west corridors
        builder.fill(new BlockVector3(3, 19, 5), new BlockVector3(3, 20, 11), AIR);
        builder.fill(new BlockVector3(4, 21, 5), new BlockVector3(4, 21, 16), SANDSTONE);
        builder.fill(new BlockVector3(17, 19, 5), new BlockVector3(17, 20, 11), AIR);
        builder.fill(new BlockVector3(16, 21, 5), new BlockVector3(16, 21, 16), SANDSTONE);
        builder.fill(new BlockVector3(2, 19, 12), new BlockVector3(2, 19, 18), SANDSTONE);
        builder.fill(new BlockVector3(18, 19, 12), new BlockVector3(18, 19, 18), SANDSTONE);
        builder.fill(new BlockVector3(3, 19, 18), new BlockVector3(18, 19, 18), SANDSTONE);
        for (int i = 0; i < 7; i++) {
            builder.setBlockStateAt(new BlockVector3(4, 19, 5 + (i << 1)), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(4, 20, 5 + (i << 1)), CHISELED_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(16, 19, 5 + (i << 1)), CUT_SANDSTONE);
            builder.setBlockStateAt(new BlockVector3(16, 20, 5 + (i << 1)), CHISELED_SANDSTONE);
        }
        // floor symbols
        builder.setBlockStateAt(new BlockVector3(9, 18, 9), TERRACOTTA_ORANGE);
        builder.setBlockStateAt(new BlockVector3(11, 18, 9), TERRACOTTA_ORANGE);
        builder.setBlockStateAt(new BlockVector3(11, 18, 11), TERRACOTTA_ORANGE);
        builder.setBlockStateAt(new BlockVector3(9, 18, 11), TERRACOTTA_ORANGE);
        builder.setBlockStateAt(new BlockVector3(10, 18, 10), TERRACOTTA_BLUE);
        builder.fill(new BlockVector3(10, 18, 7), new BlockVector3(10, 18, 8), TERRACOTTA_ORANGE);
        builder.fill(new BlockVector3(12, 18, 10), new BlockVector3(13, 18, 10), TERRACOTTA_ORANGE);
        builder.fill(new BlockVector3(10, 18, 12), new BlockVector3(10, 18, 13), TERRACOTTA_ORANGE);
        builder.fill(new BlockVector3(7, 18, 10), new BlockVector3(8, 18, 10), TERRACOTTA_ORANGE);
        // trap chamber
        builder.fill(new BlockVector3(8, 0, 8), new BlockVector3(12, 3, 12), CUT_SANDSTONE);
        builder.fill(new BlockVector3(8, 4, 8), new BlockVector3(12, 4, 12), CHISELED_SANDSTONE);
        builder.fill(new BlockVector3(8, 5, 8), new BlockVector3(12, 5, 12), CUT_SANDSTONE);
        builder.fill(new BlockVector3(8, 6, 8), new BlockVector3(12, 13, 12), SANDSTONE);
        builder.fill(new BlockVector3(9, 3, 9), new BlockVector3(11, 17, 11), AIR);
        builder.fill(new BlockVector3(9, 1, 9), new BlockVector3(11, 1, 11), TNT);
        builder.fill(new BlockVector3(9, 2, 9), new BlockVector3(11, 2, 11), CUT_SANDSTONE);
        builder.fill(new BlockVector3(10, 3, 8), new BlockVector3(10, 4, 8), AIR);
        builder.setBlockStateAt(new BlockVector3(10, 3, 7), CUT_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(10, 4, 7), CHISELED_SANDSTONE);
        builder.fill(new BlockVector3(12, 3, 10), new BlockVector3(12, 4, 10), AIR);
        builder.setBlockStateAt(new BlockVector3(13, 3, 10), CUT_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(13, 4, 10), CHISELED_SANDSTONE);
        builder.fill(new BlockVector3(10, 3, 12), new BlockVector3(10, 4, 12), AIR);
        builder.setBlockStateAt(new BlockVector3(10, 3, 13), CUT_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(10, 4, 13), CHISELED_SANDSTONE);
        builder.fill(new BlockVector3(8, 3, 10), new BlockVector3(8, 4, 10), AIR);
        builder.setBlockStateAt(new BlockVector3(7, 3, 10), CUT_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(7, 4, 10), CHISELED_SANDSTONE);
        builder.setBlockStateAt(new BlockVector3(10, 3, 10), STONE_PRESSURE_PLATE);

        builder.setBlockStateAt(new BlockVector3(10, 3, 12), CHEST_N);
        CHEST_POPULATOR.create(((BlockEntityHolder<BlockEntityChest>) builder.getBlockAt(10, 3, 12)).getOrCreateBlockEntity().getInventory(), random);
        builder.setBlockStateAt(new BlockVector3(8, 3, 10), CHEST_E);
        CHEST_POPULATOR.create(((BlockEntityHolder<BlockEntityChest>) builder.getBlockAt(8, 3, 10)).getOrCreateBlockEntity().getInventory(), random);
        builder.setBlockStateAt(new BlockVector3(10, 3, 8), CHEST_S);
        CHEST_POPULATOR.create(((BlockEntityHolder<BlockEntityChest>) builder.getBlockAt(10, 3, 8)).getOrCreateBlockEntity().getInventory(), random);
        builder.setBlockStateAt(new BlockVector3(12, 3, 10), CHEST_W);
        CHEST_POPULATOR.create(((BlockEntityHolder<BlockEntityChest>) builder.getBlockAt(12, 3, 10)).getOrCreateBlockEntity().getInventory(), random);
        manager.merge(builder);
        return true;
    }

    @Override
    public String getName() {
        return "desert_pyramid";
    }

    public AxisAlignedBB getBoundingBox() {
        return new SimpleAxisAlignedBB(0, -18, 0, 21, 29, 21);
    }

    protected static final int MIN_DISTANCE = 8;
    protected static final int MAX_DISTANCE = 32;

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
        BiomeDefinition definition = Registries.BIOME.get(biome);
        if (!definition.getTags().contains(BiomeTags.DESERT) ||
                !((chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ)) {
            return false;
        }

        if (y > 128) {
            return false;
        }

        if (!level.getBlock(x, y, z).hasTag(BlockTags.SAND)) {
            return false;
        }

        return true;
    }

    protected static class ChestPopulator extends RandomizableContainer {
        public ChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.DIAMOND, 0, 3, 1, 5))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 1, 15))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 7, 2, 15))
                    .register(new ItemEntry(Item.EMERALD, 0, 3, 1, 15))
                    .register(new ItemEntry(Item.BONE, 0, 6, 4, 25))
                    .register(new ItemEntry(Item.SPIDER_EYE, 0, 3, 1, 25))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 7, 3, 25))
                    .register(new ItemEntry(Item.LEATHER, 0, 5, 1, 20))
                    .register(new ItemEntry(Item.SADDLE, 20))
                    .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 15))
                    .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 10))
                    .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 20, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 20))
                    .register(new ItemEntry(Item.ENCHANTED_GOLDEN_APPLE, 2))
                    .register(new ItemEntry(Block.AIR, 15));
            pools.put(pool1.build(), new RollEntry(2, 4, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.BONE, 0, 8, 1, 10))
                    .register(new ItemEntry(Item.GUNPOWDER, 0, 8, 1, 10))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 8, 1, 10))
                    .register(new ItemEntry(Item.STRING, 0, 8, 1, 10))
                    .register(new ItemEntry(Item.SAND, 0, 8, 1, 10));
            this.pools.put(pool2.build(), new RollEntry(4, pool2.getTotalWeight()));


            // --- Pool 3 ---
            PoolBuilder pool3 = new PoolBuilder()
                    .register(new ItemEntry(Block.AIR, 6))
                    .register(new ItemEntry(Item.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, 0, 1, 1));
            this.pools.put(pool3.build(), new RollEntry(1, pool3.getTotalWeight()));
        }
    }

}
