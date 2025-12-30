package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.RuledObjectGenerator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.block.property.CommonBlockProperties.WEIRDO_DIRECTION;


public class ObjectSwampHut extends RuledObjectGenerator {

    protected static final BlockState SPRUCE_PLANKS = BlockSprucePlanks.PROPERTIES.getDefaultState();
    protected static final BlockState FLOWER_POT = BlockFlowerPot.PROPERTIES.getDefaultState();
    protected static final BlockState OAK_FENCE = BlockOakFence.PROPERTIES.getDefaultState();
    protected static final BlockState OAK_LOG = BlockOakLog.PROPERTIES.getDefaultState();
    protected static final BlockState CAULDRON = BlockCauldron.PROPERTIES.getDefaultState();
    protected static final BlockState CRAFTING_TABLE = BlockCraftingTable.PROPERTIES.getDefaultState();
    protected static final BlockState STAIRS_N = BlockSpruceStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(2));
    protected static final BlockState STAIRS_E = BlockSpruceStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(1));
    protected static final BlockState STAIRS_S = BlockSpruceStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(3));
    protected static final BlockState STAIRS_W = BlockSpruceStairs.PROPERTIES.getBlockState(WEIRDO_DIRECTION.createValue(0));

    @Override
    public boolean generate(BlockManager object, RandomSourceProvider rand, Vector3 position) {
        StructureHelper builder = new StructureHelper(object.getLevel(), position.asBlockVector3());
        builder.fill(new BlockVector3(1, 1, 2), new BlockVector3(5, 4, 7), SPRUCE_PLANKS, BlockAir.STATE); // hut body
        builder.fill(new BlockVector3(1, 1, 1), new BlockVector3(5, 1, 1), SPRUCE_PLANKS, BlockAir.STATE); // hut steps
        builder.fill(new BlockVector3(2, 1, 0), new BlockVector3(4, 1, 0), SPRUCE_PLANKS, BlockAir.STATE); // hut steps
        builder.fill(new BlockVector3(4, 2, 2), new BlockVector3(4, 3, 2), BlockAir.STATE); // hut door
        builder.fill(new BlockVector3(5, 3, 4), new BlockVector3(5, 3, 5), BlockAir.STATE); // left window
        builder.setBlockStateAt(1, 3, 4, Block.AIR);

        builder.setBlockStateAt(1, 3, 5, FLOWER_POT);
        if(builder.getBlockAt(1, 3, 5) instanceof BlockFlowerPot pot) {
            pot.setFlower(Item.get(Block.RED_MUSHROOM));
        }

        builder.setBlockStateAt(new BlockVector3(2, 3, 2), OAK_FENCE);
        builder.setBlockStateAt(new BlockVector3(3, 3, 7), OAK_FENCE);

        builder.fill(new BlockVector3(0, 4, 1), new BlockVector3(6, 4, 1), STAIRS_N); // N
        builder.fill(new BlockVector3(6, 4, 2), new BlockVector3(6, 4, 7), STAIRS_E); // E
        builder.fill(new BlockVector3(0, 4, 8), new BlockVector3(6, 4, 8), STAIRS_S); // S
        builder.fill(new BlockVector3(0, 4, 2), new BlockVector3(0, 4, 7), STAIRS_W); // W

        builder.fill(new BlockVector3(1, 0, 2), new BlockVector3(1, 3, 2), OAK_LOG);
        builder.fill(new BlockVector3(5, 0, 2), new BlockVector3(5, 3, 2), OAK_LOG);
        builder.fill(new BlockVector3(1, 0, 7), new BlockVector3(1, 3, 7), OAK_LOG);
        builder.fill(new BlockVector3(5, 0, 7), new BlockVector3(5, 3, 7), OAK_LOG);

        builder.setBlockStateAt(new BlockVector3(1, 2, 1), OAK_FENCE);
        builder.setBlockStateAt(new BlockVector3(5, 2, 1), OAK_FENCE);

        builder.setBlockStateAt(new BlockVector3(4, 2, 6), CAULDRON);

        builder.setBlockStateAt(new BlockVector3(3, 2, 6), CRAFTING_TABLE);

        builder.setBlockDownward(new BlockVector3(1, -1, 2), OAK_LOG);
        builder.setBlockDownward(new BlockVector3(5, -1, 2), OAK_LOG);
        builder.setBlockDownward(new BlockVector3(1, -1, 7), OAK_LOG);
        builder.setBlockDownward(new BlockVector3(5, -1, 7), OAK_LOG);
        object.merge(builder);
        return true;
    }

    @Override
    public String getName() {
        return "monster_room";
    }

    @Override
    public boolean canGenerateAt(Location location) {
        int x = location.getFloorX();
        int y = location.getFloorY();
        int z = location.getFloorZ();
        Level level = location.getLevel();
        random.setSeed(level.getSeed() ^ (x+y+z));

        chance:
        for (int chance = 0; chance < 8; ++chance) {
            int xv = random.nextBoundedInt(2) + 2;
            int x1 = -xv - 1;
            int x2 = xv + 1;

            int zv = random.nextBoundedInt(2) + 2;
            int z1 = -zv - 1;
            int z2 = zv + 1;

            int t = 0;

            for (int dx = x1; dx <= x2; ++dx) {
                for (int dy = -1; dy <= 4; ++dy) {
                    for (int dz = z1; dz <= z2; ++dz) {
                        int tx = x + dx;
                        int ty = y + dy;
                        int tz = z + dz;

                        Block block = level.getBlock(tx, ty, tz);
                        boolean isSolid = block.isSolid();

                        if (dy == -1 && !isSolid) {
                            continue chance;
                        }
                        if (dy == 4 && !isSolid) {
                            continue chance;
                        }
                        if ((dx == x1 || dx == x2 || dz == z1 || dz == z2) && dy == 0 && level.getBlockIdAt(tx, ty + 1, tz) == AIR) {
                            ++t;
                        }
                    }
                }
            }

            if (t >= 1 && t <= 5) {
                return true;
            }
        }
        return false;
    }

    protected static class ChestPopulator extends RandomizableContainer {
        public ChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.SADDLE, 20))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 15))
                    .register(new ItemEntry(Item.ENCHANTED_GOLDEN_APPLE, 2))
                    .register(new ItemEntry(Item.MUSIC_DISC_13, 15))
                    .register(new ItemEntry(Item.MUSIC_DISC_CAT, 15))
                    .register(new ItemEntry(Item.NAME_TAG, 20))
                    .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 10))
                    .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 15))
                    .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 10, getDefaultEnchantments()));
            this.pools.put(pool1.build(), new RollEntry(3, 1, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 4, 10))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 4, 5))
                    .register(new ItemEntry(Item.BREAD, 20))
                    .register(new ItemEntry(Block.WHEAT, 0, 4, 20))
                    .register(new ItemEntry(Item.BUCKET, 10))
                    .register(new ItemEntry(Item.REDSTONE, 0, 4, 15))
                    .register(new ItemEntry(Item.COAL, 0, 4, 15))
                    .register(new ItemEntry(Item.MELON_SEEDS, 0, 4, 2, 10))
                    .register(new ItemEntry(Item.PUMPKIN_SEEDS, 0, 4, 2, 10))
                    .register(new ItemEntry(Item.BEETROOT_SEEDS, 0, 4, 2, 10));
            this.pools.put(pool2.build(), new RollEntry(4, 1, pool2.getTotalWeight()));

            PoolBuilder pool3 = new PoolBuilder()
                    .register(new ItemEntry(Item.BONE, 0, 8, 10))
                    .register(new ItemEntry(Item.GUNPOWDER, 0, 8, 10))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 8, 10))
                    .register(new ItemEntry(Item.STRING, 0, 8, 10));
            this.pools.put(pool3.build(), new RollEntry(3, pool3.getTotalWeight()));
        }
    }
}
