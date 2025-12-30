package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.block.BlockStructureBlock;
import cn.nukkit.block.BlockWater;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.ChunkPosition;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;
import com.google.common.collect.Lists;

import java.util.List;

public class OceanRuinPopulator extends Populator {

    public static final String NAME = "normal_ocean_ruin";

    private static final SmallChestPopulator SMALL_CHEST_POPULATOR = new SmallChestPopulator();
    private static final LargeChestPopulator LARGE_CHEST_POPULATOR = new LargeChestPopulator();

    protected static final List<ChunkPosition> ADJACENT_CHUNKS = Lists.newArrayList(
            new ChunkPosition(-1, 0, -1),
            new ChunkPosition(-1, 0, 0),
            new ChunkPosition(-1, 0, 1),
            new ChunkPosition(0, 0, -1),
            new ChunkPosition(0, 0, 1),
            new ChunkPosition(1, 0, -1),
            new ChunkPosition(1, 0, 0),
            new ChunkPosition(1, 0, 1)
    );

    protected static final int SPACING = 20;
    protected static final int SEPARATION = 8;
    protected static final PNXStructure[] WARM_RUINS = {
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_1"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_2"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_3"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_4"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_5"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_6"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_7"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/warm_8")
    };
    protected static final PNXStructure[] RUINS_BRICK = {
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_1"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_2"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_3"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_4"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_5"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_6"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_7"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/brick_8")
    };
    protected static final PNXStructure[] RUINS_CRACKED = { //70
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_1"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_2"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_3"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_4"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_5"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_6"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_7"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/cracked_8")
    };
    protected static final PNXStructure[] RUINS_MOSSY = { //50
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_1"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_2"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_3"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_4"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_5"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_6"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_7"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/mossy_8")
    };
    protected static final PNXStructure[] BIG_WARM_RUINS = {
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_warm_4"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_warm_5"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_warm_6"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_warm_7"),
    };
    protected static final PNXStructure[] BIG_RUINS_BRICK = {
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_brick_1"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_brick_2"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_brick_3"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_brick_8"),
    };
    protected static final PNXStructure[] BIG_RUINS_MOSSY = {
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_mossy_1"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_mossy_2"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_mossy_3"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_mossy_8"),
    };
    protected static final PNXStructure[] BIG_RUINS_CRACKED = {
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_cracked_1"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_cracked_2"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_cracked_3"),
            (PNXStructure) Registries.STRUCTURE.get("underwater_ruin/big_cracked_8"),
    };

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
        BiomeDefinition definition = Registries.BIOME.get(biome);
        if (definition.getTags().contains(BiomeTags.OCEAN)
                && chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt((SPACING - SEPARATION) -1)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt((SPACING - SEPARATION) - 1)) {
            boolean isWarm = definition.getTags().contains(BiomeTags.WARM);
            boolean isLarge = random.nextBoundedInt(100) <= 30;

            PNXStructure template;
            int index;

            if (isWarm) {
                index = -1;
                if (isLarge) {
                    template = BIG_WARM_RUINS[random.nextBoundedInt(BIG_WARM_RUINS.length-1)];
                } else {
                    template = WARM_RUINS[random.nextBoundedInt(WARM_RUINS.length-1)];
                }
            } else if (isLarge) {
                index = random.nextBoundedInt(BIG_RUINS_BRICK.length-1);
                template = BIG_RUINS_BRICK[index];
            } else {
                index = random.nextBoundedInt(RUINS_BRICK.length-1);
                template = RUINS_BRICK[index];
            }
            BlockManager manager = new BlockManager(level);
            this.placeRuin(template, chunk, random.nextInt(), isLarge, index, manager);

            if (isLarge && random.nextBoundedInt(100) <= 90) {
                List<ChunkPosition> adjacentChunks = Lists.newArrayList(ADJACENT_CHUNKS);
                for (int i = 0; i < random.nextInt(4, 8); i++) {
                    ChunkPosition chunkPos = adjacentChunks.remove(random.nextBoundedInt(adjacentChunks.size()-1));
                    this.placeAdjacentRuin(level.getChunk(chunkX + chunkPos.x, chunkZ + chunkPos.z), random, isWarm, manager);
                }
            }
            for(Block block : manager.getBlocks()) {
                if(block instanceof BlockAir) manager.unsetBlockStateAt(block);
                if(block instanceof BlockStructureBlock) {
                    manager.setBlockStateAt(block, BlockChest.PROPERTIES.getDefaultState());
                    level.getScheduler().scheduleDelayedTask(() -> {
                        BlockChest chest = (BlockChest) manager.getBlockAt(block);
                        RandomizableContainer container = isLarge ? LARGE_CHEST_POPULATOR : SMALL_CHEST_POPULATOR;
                        container.create(chest.getOrCreateBlockEntity().getInventory(), random);
                    }, 10);
                }
                if(block instanceof BlockMagma) {
                    level.getScheduler().scheduleDelayedTask(() -> {
                        level.getBlock(block).onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }, 10);
                }
                //WaterLogging does not work with BlockManager. Therefore, we set the water in the level.
                manager.getLevel().setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), 1, BlockWater.PROPERTIES.getDefaultState());
            }
            queueObject(chunk, manager);
        }
    }

    protected void placeAdjacentRuin(IChunk chunk, RandomSourceProvider random, boolean isWarm, BlockManager manager) {
        PNXStructure template;
        int index;

        if (isWarm) {
            template = WARM_RUINS[random.nextBoundedInt(WARM_RUINS.length-1)];
            index = -1;
        } else {
            index = random.nextBoundedInt(RUINS_BRICK.length-1);
            template = RUINS_BRICK[random.nextBoundedInt(RUINS_BRICK.length-1)];
        }

        int seed = random.nextInt();

        this.placeRuin(template, chunk, seed, false, index, manager);

    }

    protected void placeRuin(PNXStructure template, IChunk chunk, int seed, boolean isLarge, int index, BlockManager manager) {
        NukkitRandom random = new NukkitRandom(seed);

        BlockVector3 size = new BlockVector3(template.getSizeX(), template.getSizeY(), template.getSizeZ());
        int x = random.nextBoundedInt(16 - size.getX());
        int z = random.nextBoundedInt(16 - size.getZ());
        int y = 256;

        for (int cx = x; cx < x + size.getX(); cx++) {
            for (int cz = z; cz < z + size.getZ(); cz++) {
                int h = chunk.getHeightMap(cx, cz);

                Block id = chunk.getBlockState(cx, h, cz).toBlock();
                while (id.canBeReplaced() && h > 1) {
                    id = chunk.getBlockState(cx, --h, cz).toBlock();
                }

                y = Math.min(h, y);
            }
        }

        Position vec = new Position((chunk.getX() << 4) + x, y, (chunk.getZ() << 4) + z);
        BlockManager manager1 = new BlockManager(manager.getLevel());
        template.preparePlace(vec, manager1);
        for(Block block : manager1.getBlocks()) {
            int ran = random.nextInt(10);
            if(ran == 0) manager1.unsetBlockStateAt(block);
            if(ran == 1 && !isLarge) manager1.unsetBlockStateAt(block);
        }
        manager.merge(manager1);
        if (index != -1) {
            PNXStructure mossy;
            PNXStructure cracked;

            if (isLarge) {
                mossy = BIG_RUINS_MOSSY[index];
                cracked = BIG_RUINS_CRACKED[index];
            } else {
                mossy = RUINS_MOSSY[index];
                cracked = RUINS_CRACKED[index];
            }

            BlockManager manager2 = new BlockManager(manager.getLevel());
            mossy.preparePlace(vec, manager2);
            for(Block block : manager2.getBlocks()) {
                int ran = random.nextInt(10);
                if(ran < 3) manager2.unsetBlockStateAt(block);
            }
            manager.merge(manager2);
            BlockManager manager3 = new BlockManager(manager.getLevel());
            cracked.preparePlace(vec, manager3);
            for(Block block : manager3.getBlocks()) {
                if(random.nextBoolean()) manager3.unsetBlockStateAt(block);
            }
            manager.merge(manager3);
        }
    }

    protected static class SmallChestPopulator extends RandomizableContainer {
        public SmallChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.COAL, 0, 4, 10))
                    .register(new ItemEntry(Item.STONE_AXE, 2))
                    .register(new ItemEntry(Item.ROTTEN_FLESH, 5))
                    .register(new ItemEntry(Item.EMERALD, 1))
                    .register(new ItemEntry(Block.WHEAT, 0, 3, 2, 10));
            this.pools.put(pool1.build(), new RollEntry(8, 2, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.LEATHER_CHESTPLATE, 1))
                    .register(new ItemEntry(Item.GOLDEN_HELMET, 1))
                    .register(new ItemEntry(Item.FISHING_ROD, 0, 1, 1, 5, getRodEnchantments()))
                    .register(new ItemEntry(Item.EMPTY_MAP, 5)); //TODO: exploration_map buried treasure
            this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
        }
    }

    protected static class LargeChestPopulator extends RandomizableContainer {
        public LargeChestPopulator() {
            PoolBuilder pool1 = new PoolBuilder()
                    .register(new ItemEntry(Item.COAL, 0, 4, 10))
                    .register(new ItemEntry(Item.GOLD_NUGGET, 0, 3, 10))
                    .register(new ItemEntry(Item.EMERALD, 1))
                    .register(new ItemEntry(Block.WHEAT, 0, 3, 2, 10));
            this.pools.put(pool1.build(), new RollEntry(8, 2, pool1.getTotalWeight()));

            PoolBuilder pool2 = new PoolBuilder()
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 1))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 5, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.LEATHER_CHESTPLATE, 1))
                    .register(new ItemEntry(Item.GOLDEN_HELMET, 1))
                    .register(new ItemEntry(Item.FISHING_ROD, 0, 1,1,5, getRodEnchantments()))
                    .register(new ItemEntry(Item.EMPTY_MAP, 10)); //TODO: exploration_map buried treasure
            this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
        }
    }

    private static Enchantment[] getRodEnchantments() {
        return new Enchantment[]{
                Enchantment.getEnchantment(Enchantment.ID_LURE),
                Enchantment.getEnchantment(Enchantment.ID_DURABILITY),
                Enchantment.getEnchantment(Enchantment.ID_FORTUNE_FISHING),
                Enchantment.getEnchantment(Enchantment.ID_MENDING)
        };
    }

}
