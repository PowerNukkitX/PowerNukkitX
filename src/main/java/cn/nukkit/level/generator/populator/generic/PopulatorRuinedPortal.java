package cn.nukkit.level.generator.populator.generic;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;
import java.util.Set;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class PopulatorRuinedPortal extends Populator {

    public static final String NAME = "generic_ruined_portal";

    private static BlockState NETHERRACK = BlockNetherrack.PROPERTIES.getDefaultState();
    private static BlockState CRYING_OBSIDIAN = BlockCryingObsidian.PROPERTIES.getDefaultState();
    private static BlockState POLISHED_BLACKSTONE_BRICKS = BlockPolishedBlackstoneBricks.PROPERTIES.getDefaultState();
    private static BlockState POLISHED_BLACKSTONE_BRICK_SLAB = BlockPolishedBlackstoneBrickSlab.PROPERTIES.getDefaultState();
    private static BlockState POLISHED_BLACKSTONE_BRICK_STAIRS = BlockPolishedBlackstoneBrickStairs.PROPERTIES.getDefaultState();
    private static BlockState CHISELED_POLISHED_BLACKSTONE = BlockChiseledPolishedBlackstone.PROPERTIES.getDefaultState();
    private static BlockState POLISHED_BLACKSTONE_WALL = BlockPolishedBlackstoneWall.PROPERTIES.getDefaultState();
    private static BlockState CRACKED_POLISHED_BLACKSTONE_BRICKS = BlockCrackedPolishedBlackstoneBricks.PROPERTIES.getDefaultState();

    private static ChestPopulator CHEST_POPULATOR = new ChestPopulator();

    protected static final int MIN_DISTANCE = 40;
    protected static final int MAX_DISTANCE = 25;

    private static final String[] PORTALS = new String[]{
            "ruined_portal/portal_1",
            "ruined_portal/portal_2",
            "ruined_portal/portal_3",
            "ruined_portal/portal_4",
            "ruined_portal/portal_5",
            "ruined_portal/portal_6",
            "ruined_portal/portal_7",
            "ruined_portal/portal_8",
            "ruined_portal/portal_9",
            "ruined_portal/portal_10"
    };
    private static final String[] GIANT_PORTALS = new String[]{
            "ruined_portal/giant_portal_1",
            "ruined_portal/giant_portal_2",
            "ruined_portal/giant_portal_3"
    };

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(canGenerate(random, chunk)) {
            int x = (chunkX << 4) + 7;
            int z = (chunkZ << 4) + 7;
            BiomeDefinition definition = Registries.BIOME.get(chunk.getBiomeId(7, SEA_LEVEL, 7));
            Set<String> tags = definition.getTags();
            PortalHeight height = null;
            if(tags.contains(BiomeTags.DESERT)) height = PortalHeight.PARTLY_BURIED;
            else if(tags.contains(BiomeTags.JUNGLE) || tags.contains(BiomeTags.SWAMP)) height = PortalHeight.ON_LAND_SURFACE;
            else if(tags.contains(BiomeTags.MOUNTAINS)) height = random.nextBoolean() ? PortalHeight.ON_LAND_SURFACE : PortalHeight.IN_MOUNTAIN;
            else if(tags.contains(BiomeTags.OCEAN)) height = PortalHeight.ON_OCEAN_FLOOR;
            else if(tags.contains(BiomeTags.NETHER)) height = PortalHeight.IN_NETHER;
            else height = random.nextBoolean() ? PortalHeight.ON_LAND_SURFACE : PortalHeight.UNDERGROUND;
            boolean big = random.nextBoundedInt(20) == 0;
            PNXStructure structure = (PNXStructure) Registries.STRUCTURE.get(big ? GIANT_PORTALS[random.nextInt(GIANT_PORTALS.length)] : PORTALS[random.nextInt(PORTALS.length)]);
            int y = findSuitableY(random, chunk, height, chunk.getHeightMap(7, 7), structure.getSizeY());
            BlockManager manager = new BlockManager(level);
            structure.preparePlace(new Position(x, y, z), manager);
            for(Block block : manager.getBlocks()) {
                if(block instanceof BlockJigsaw) manager.setBlockStateAt(block, NETHERRACK);
                if(level.getBlock(block) instanceof BlockFlowingWater) {
                    //WaterLogging does not work with BlockManager. Therefore, we set the water in the level.
                    manager.getLevel().setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), 1, BlockWater.PROPERTIES.getDefaultState());
                }
                if(block instanceof BlockObsidian) {
                    if(random.nextInt(5) == 0) {
                        manager.setBlockStateAt(block, CRYING_OBSIDIAN);
                    }
                }
                if(block instanceof BlockMagma) {
                    level.getScheduler().scheduleDelayedTask(() -> {
                        level.getBlock(block).onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }, 10);
                }
                if(block instanceof BlockChest chest) {
                    level.getScheduler().scheduleDelayedTask(() -> {
                        CHEST_POPULATOR.create(chest.getOrCreateBlockEntity().getInventory(), random);
                    }, 10);
                }
                if(level.getDimension() == Level.DIMENSION_NETHER) {
                    if(block instanceof BlockStoneBricks) {
                        manager.setBlockStateAt(block, POLISHED_BLACKSTONE_BRICKS);
                    }
                    else if(block instanceof BlockStoneBrickSlab) {
                        manager.setBlockStateAt(block, POLISHED_BLACKSTONE_BRICK_SLAB.setPropertyValues(block.getProperties()));
                    }
                    else if(block instanceof BlockStoneBrickStairs) {
                        manager.setBlockStateAt(block, POLISHED_BLACKSTONE_BRICK_STAIRS.setPropertyValues(block.getProperties()));
                    }
                    else if(block instanceof BlockChiseledStoneBricks) {
                        manager.setBlockStateAt(block, CHISELED_POLISHED_BLACKSTONE.setPropertyValues(block.getProperties()));
                    }
                    else if(block instanceof BlockStoneBrickWall) {
                        manager.setBlockStateAt(block, POLISHED_BLACKSTONE_WALL.setPropertyValues(block.getProperties()));
                    }
                    else if(block instanceof BlockCrackedStoneBricks) {
                        manager.setBlockStateAt(block, CRACKED_POLISHED_BLACKSTONE_BRICKS.setPropertyValues(block.getProperties()));
                    } else if(block instanceof BlockMossyStoneBricks) {
                        manager.unsetBlockStateAt(block);
                    }
                }
            }
            manager.generateChunks();
            queueObject(chunk, manager);
        }
    }


    private static int findSuitableY(
            RandomSourceProvider random,
            IChunk chunk,
            PortalHeight portalHeight,
            int height,
            int structureHeight) {
        int j = 15;
        int i;
        if (portalHeight == PortalHeight.IN_NETHER) {
            if (random.nextBoolean()) {
                i = NukkitMath.randomRange(random, 27, 29);
            } else {
                i = NukkitMath.randomRange(random, 29, 100);
            }
        } else if (portalHeight == PortalHeight.IN_MOUNTAIN) {
            int k = height - structureHeight;
            i = getRandomWithinInterval(random, 70, k);
        } else if (portalHeight == PortalHeight.UNDERGROUND) {
            int j1 = height - structureHeight;
            i = getRandomWithinInterval(random, j, j1);
        } else if (portalHeight == PortalHeight.PARTLY_BURIED) {
            i = height - structureHeight + NukkitMath.randomRange(random, 2, 8);
        } else {
            i = height;
        }

        int y = i;
        Block id = chunk.getBlockState(7, y, 7).toBlock();
        while (id.canBeReplaced()) {
            id = chunk.getBlockState(7, --y, 7).toBlock();
        }
        return y;
    }

    private static int getRandomWithinInterval(RandomSourceProvider random, int start, int end) {
        return start < end ? NukkitMath.randomRange(random, start, end) : end;
    }

    public boolean canGenerate(RandomSourceProvider random, IChunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        return ((chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ);
    }

    @Override
    public String name() {
        return NAME;
    }

    protected static class ChestPopulator extends RandomizableContainer {

        public ChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Block.OBSIDIAN, 1, 2, 40))
                    .register(new ItemEntry(Item.FLINT, 1, 4, 40))
                    .register(new ItemEntry(Item.IRON_NUGGET, 9, 18, 40))
                    .register(new ItemEntry(Item.FLINT_AND_STEEL, 40))
                    .register(new ItemEntry(Item.FIRE_CHARGE, 40))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 15))
                    .register(new ItemEntry(Item.GOLD_NUGGET, 4, 24, 15))
                    .register(new ItemEntry(Item.GOLDEN_SWORD, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_AXE, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_HOE, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_SHOVEL, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_PICKAXE, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_BOOTS, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_CHESTPLATE, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_HELMET, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_LEGGINGS, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GLISTERING_MELON_SLICE, 4, 12, 5))
                    .register(new ItemEntry(Item.GOLDEN_HORSE_ARMOR, 5))
                    .register(new ItemEntry(Block.LIGHT_WEIGHTED_PRESSURE_PLATE, 5))
                    .register(new ItemEntry(Item.GOLDEN_CARROT, 4, 12, 5))
                    .register(new ItemEntry(Item.CLOCK, 5))
                    .register(new ItemEntry(Item.GOLD_INGOT, 2, 8, 5))
                    .register(new ItemEntry(Block.BELL, 1))
                    .register(new ItemEntry(Item.ENCHANTED_GOLDEN_APPLE, 1))
                    .register(new ItemEntry(Block.GOLD_BLOCK, 1, 2, 1));

            this.pools.put(pool1.build(), new RollEntry(8, 4, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Block.AIR, 1))
                    .register(new ItemEntry(Block.LODESTONE, 1, 2, 2));

            this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
        }
    }

    protected enum PortalHeight {
        ON_LAND_SURFACE,
        PARTLY_BURIED,
        ON_OCEAN_FLOOR,
        IN_MOUNTAIN,
        UNDERGROUND,
        IN_NETHER
    }
}
