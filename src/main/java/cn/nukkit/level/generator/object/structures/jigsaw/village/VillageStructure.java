package cn.nukkit.level.generator.object.structures.jigsaw.village;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockCobblestone;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockJigsaw;
import cn.nukkit.block.BlockSnow;
import cn.nukkit.block.BlockSnowLayer;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.JigsawStructure;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.block.BlockState;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.nukkit.block.BlockID.*;

/**
 * Villages for PowerNukkitX
 * @author Buddelbubi
 */
public abstract class VillageStructure extends JigsawStructure {

    private static final Object2ObjectMap<String, String> VILLAGE_LOOT_CATEGORY_LOOKUP;
    private final Map<BlockVector3, RandomizableContainer> pendingChestLoot = new HashMap<>();

    static {
        VILLAGE_LOOT_CATEGORY_LOOKUP = new Object2ObjectArrayMap<>();
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("armorer", "armorer");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("butcher", "butcher");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("cartographer", "cartographer");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("fletcher", "fletcher");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("mason", "mason");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("shepherd", "shepherd");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("tannery", "tannery");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("temple", "temple");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("tool_smith", "toolsmith");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("toolsmith", "toolsmith");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("weapon_smith", "weaponsmith");
        VILLAGE_LOOT_CATEGORY_LOOKUP.put("weaponsmith", "weaponsmith");
    }

    @Override
    protected void postProcessStructure(StructureHelper helper) {
        List<Block> placedBlocks = new ArrayList<>(helper.getBlocks());
        Level level = helper.getLevel();
        helper.addHook(() -> {
            populatePendingChestLoot(level);
        });
        helper.ubChunkUpdate();

        placedBlocks.stream()
                .filter(BlockUnknown.class::isInstance)
                .forEach(block -> level.setBlock(block, BlockAir.STATE.toBlock(block), true, true));

        for (Block block : placedBlocks) {
            if (!(level.getBlock(block) instanceof BlockJigsaw)) {
                continue;
            }
            level.setBlock(block, BlockAir.STATE.toBlock(block), true, true);
            Entity villager = Entity.createEntity(
                    Entity.VILLAGER_V2,
                    new Position(block.getFloorX() + 0.5, block.getFloorY(), block.getFloorZ() + 0.5, level)
            );
            if (villager != null) {
                villager.spawnToAll();
            }
        }
    }

    @Override
    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        liftPieceAboveWater(blockManager, jigsaws);
        if (structureName.contains("lamp")) {
            shiftWholePieceToTerrain(blockManager, jigsaws);
            int lampHeightOffset = getLampHeightOffset(structureName);
            if (lampHeightOffset != 0) {
                shiftWholePiece(blockManager, jigsaws, lampHeightOffset);
            }
            for(Block block : blockManager.getBlocks()) {
                if(block.isAir()) blockManager.unsetBlockStateAt(block);
            }
            return;
        }
        if (structureName.contains("/streets/") || structureName.contains("/terminators/")) {
            adaptStreetColumnsToTerrain(blockManager, jigsaws);
            return;
        }
        if (structureName.contains("/houses/")) {
            if (shouldShiftHousesToTerrain()) {
                shiftWholePieceToTerrain(blockManager, jigsaws);
            }
            if (usesDirtSupports(structureName)) {
                fillPieceSupports(blockManager, BlockDirt.PROPERTIES.getDefaultState());
            } else {
                fillPieceSupports(blockManager, getHouseSupportState());
            }
            registerVillageChestLoot(structureName, blockManager);
            return;
        }
        fillPieceSupports(blockManager, BlockDirt.PROPERTIES.getDefaultState());
    }

    protected void registerVillageChestLoot(String structureName, BlockManager blockManager) {
        RandomizableContainer loot = resolveVillageChestLoot(structureName);
        if (loot == null) {
            return;
        }
        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockChest) {
                pendingChestLoot.put(block.asBlockVector3(), loot);
            }
        }
    }

    protected RandomizableContainer resolveVillageChestLoot(String structureName) {
        String lootCategory = getVillageLootCategory(structureName);
        if (lootCategory == null) {
            return null;
        }
        return switch (lootCategory) {
            case "armorer" -> VillageChestLoot.ARMORER;
            case "butcher" -> VillageChestLoot.BUTCHER;
            case "cartographer" -> VillageChestLoot.CARTOGRAPHER;
            case "fletcher" -> VillageChestLoot.FLETCHER;
            case "mason" -> VillageChestLoot.MASON;
            case "shepherd" -> VillageChestLoot.SHEPHERD;
            case "tannery" -> VillageChestLoot.TANNERY;
            case "temple" -> VillageChestLoot.TEMPLE;
            case "toolsmith" -> VillageChestLoot.TOOLSMITH;
            case "weaponsmith" -> VillageChestLoot.WEAPONSMITH;
            default -> null;
        };
    }

    protected String getVillageLootCategory(String structureName) {
        for (Map.Entry<String, String> entry : VILLAGE_LOOT_CATEGORY_LOOKUP.object2ObjectEntrySet()) {
            if (structureName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    protected boolean isGenericVillageHouse(String structureName, String biome) {
        return structureName.contains("/houses/" + biome + "_small_house_")
                || structureName.contains("/houses/" + biome + "_medium_house_")
                || structureName.contains("/houses/" + biome + "_big_house_");
    }

    protected void populatePendingChestLoot(Level level) {
        for (Map.Entry<BlockVector3, RandomizableContainer> entry : pendingChestLoot.entrySet()) {
            BlockVector3 pos = entry.getKey();
            Block block = level.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (!(block instanceof BlockChest chest)) {
                continue;
            }
            BlockEntityChest blockEntity = chest.getOrCreateBlockEntity();
            Inventory inventory = blockEntity.getInventory();
            inventory.clearAll();
            entry.getValue().create(inventory, createVillageLootRandom(level, pos));
        }
        pendingChestLoot.clear();
    }

    protected RandomSourceProvider createVillageLootRandom(Level level, BlockVector3 pos) {
        long seed = level.getSeed();
        seed ^= 0x9E3779B97F4A7C15L * pos.getX();
        seed ^= 0xC2B2AE3D27D4EB4FL * pos.getY();
        seed ^= 0x165667B19E3779F9L * pos.getZ();
        return new Xoroshiro128(seed);
    }

    protected boolean shouldShiftHousesToTerrain() {
        return false;
    }

    protected BlockState getHouseSupportState() {
        return BlockCobblestone.PROPERTIES.getDefaultState();
    }

    protected int getLampHeightOffset(String structureName) {
        return 0;
    }

    protected void liftPieceAboveWater(BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        Level level = blockManager.getLevel();
        Block globalLowestBlock = null;
        Map<Long, Integer> lowestColumns = new HashMap<>();

        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockJigsaw || block.isAir()) {
                continue;
            }
            if (globalLowestBlock == null || block.getFloorY() < globalLowestBlock.getFloorY()) {
                globalLowestBlock = block;
            }
        }

        if (globalLowestBlock == null) {
            return;
        }

        int supportY = globalLowestBlock.getFloorY();
        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockJigsaw || block.isAir() || block.getFloorY() != supportY) {
                continue;
            }
            lowestColumns.put(columnKey(block.getFloorX(), block.getFloorZ()), 1);
        }

        int deltaY = 0;
        for (long columnKey : lowestColumns.keySet()) {
            int x = (int) (columnKey >> 32);
            int z = (int) columnKey;
            IChunk chunk = level.getChunk(x >> 4, z >> 4);
            if (!chunk.isGenerated()) {
                level.syncGenerateChunk(chunk.getX(), chunk.getZ());
            }

            int height = level.getHeightMap(x, z);
            Block topBlock = level.getBlock(x, height, z);
            if (!(topBlock instanceof BlockFlowingWater) && !topBlock.isWaterLogged()) {
                continue;
            }

            deltaY = Math.max(deltaY, height + 1 - supportY);
        }

        if (deltaY <= 0) {
            return;
        }

        shiftWholePiece(blockManager, jigsaws, deltaY);
    }

    protected void shiftWholePieceToTerrain(BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        List<Block> blocks = new ArrayList<>(blockManager.getBlocks());
        if (blocks.isEmpty()) {
            return;
        }

        Block anchor = blocks.stream()
                .filter(block -> !(block instanceof BlockJigsaw))
                .min(Comparator.comparingInt(Vector3::getFloorY))
                .orElse(blocks.getFirst());

        int targetY = getPlacementY(blockManager.getLevel(), anchor.getFloorX(), anchor.getFloorZ());
        int deltaY = targetY - anchor.getFloorY();
        if (deltaY == 0) {
            return;
        }

        shiftWholePiece(blockManager, jigsaws, deltaY);
    }

    protected void shiftWholePiece(BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws, int deltaY) {
        List<Block> blocks = new ArrayList<>(blockManager.getBlocks());
        for (Block block : blocks) {
            blockManager.unsetBlockStateAt(block);
        }
        for (Block block : blocks) {
            if (block instanceof BlockJigsaw) {
                continue;
            }
            blockManager.setBlockStateAt(block.getFloorX(), block.getFloorY() + deltaY, block.getFloorZ(), block.getBlockState());
        }
        for (PNXStructure.Jigsaw jigsaw : jigsaws) {
            jigsaw.y += deltaY;
        }
    }

    protected void adaptStreetColumnsToTerrain(BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        Level level = blockManager.getLevel();
        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockJigsaw) {
                blockManager.unsetBlockStateAt(block);
                continue;
            }
            IChunk chunk = level.getChunk(block.getChunkX(), block.getChunkZ());
            if (!chunk.isGenerated()) {
                level.syncGenerateChunk(chunk.getX(), chunk.getZ());
            }
            int height = getTerrainY(level, block.getFloorX(), block.getFloorZ());
            blockManager.unsetBlockStateAt(block);
            if (isStreet(block)) {
                if(!blockManager.isCached(new BlockVector3(block.getFloorX(), height+1, block.getFloorZ()))) {
                    blockManager.setBlockStateAt(block.getFloorX(), height + 1, block.getFloorZ(), BlockAir.STATE);
                    blockManager.setBlockStateAt(block.getFloorX(), height + 2, block.getFloorZ(), BlockAir.STATE);
                }
                blockManager.setBlockStateAt(block.getFloorX(), height, block.getFloorZ(), block.getBlockState());
                int finalHeight = height;
                Arrays.stream(jigsaws)
                        .filter(jigsaw -> jigsaw.x == block.getFloorX() && jigsaw.z == block.getFloorZ())
                        .findFirst()
                        .ifPresent(jigsaw -> jigsaw.y = finalHeight + 1);
            }
        }
    }

    protected boolean usesDirtSupports(String structureName) {
        return structureName.contains("_farm_")
                || structureName.contains("_stable_")
                || structureName.contains("_animal_pen_")
                || structureName.contains("_accessory_");
    }

    protected void fillPieceSupports(BlockManager blockManager, cn.nukkit.block.BlockState supportState) {
        Level level = blockManager.getLevel();
        Block globalLowestBlock = null;
        Map<Long, Integer> supportedColumns = new HashMap<>();

        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockJigsaw || block.isAir()) {
                continue;
            }

            if (globalLowestBlock == null || block.getFloorY() < globalLowestBlock.getFloorY()) {
                globalLowestBlock = block;
            }
        }

        if (globalLowestBlock == null) {
            return;
        }

        int supportY = globalLowestBlock.getFloorY();
        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockJigsaw || block.isAir() || block.getFloorY() != supportY) {
                continue;
            }
            supportedColumns.put(columnKey(block.getFloorX(), block.getFloorZ()), 1);
        }

        for (long columnKey : supportedColumns.keySet()) {
            int x = (int) (columnKey >> 32);
            int z = (int) columnKey;
            IChunk chunk = level.getChunk(x >> 4, z >> 4);
            if (!chunk.isGenerated()) {
                level.syncGenerateChunk(chunk.getX(), chunk.getZ());
            }

            for (int y = supportY - 1; y >= level.getMinHeight(); y--) {
                Block worldBlock = blockManager.getBlockIfCachedOrLoaded(x, y, z);
                if (worldBlock.isSolid() && !isReplaceableTerrainCover(worldBlock)) {
                    break;
                }

                blockManager.setBlockStateAt(
                        x,
                        y,
                        z,
                        supportState
                );
            }
        }
    }

    protected long columnKey(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    protected int getTerrainY(Level level, int x, int z) {
        int height = level.getHeightMap(x, z);
        while (isReplaceableTerrainCover(level.getBlock(x, height, z))
                || level.getBlock(x, height, z).canBeReplaced()
                || level.getBlock(x, height, z).isTransparent()) {
            height--;
        }
        return height;
    }

    protected int getPlacementY(Level level, int x, int z) {
        int height = level.getHeightMap(x, z);
        Block topBlock = level.getBlock(x, height, z);
        if (topBlock instanceof BlockFlowingWater || topBlock.isWaterLogged()) {
            return height + 1;
        }
        return getTerrainY(level, x, z) + 1;
    }

    protected boolean isReplaceableTerrainCover(Block block) {
        return block instanceof BlockSnow || block instanceof BlockSnowLayer;
    }

    protected boolean isStreet(Block block) {
        return switch (block.getId()) {
            case GRASS_PATH,
                 GRASS_BLOCK -> true;
            default -> false;
        };
    }

    @Override
    protected int getMaxDepth() {
        return 7;
    }

    protected static class VillageChestLoot extends RandomizableContainer {
        static final VillageChestLoot ARMORER = create(false, 1, 5,
                item(ItemID.IRON_INGOT, 0, 3, 1, 2),
                item(ItemID.BREAD, 0, 4, 1, 4),
                item(ItemID.IRON_HELMET, 1),
                item(ItemID.EMERALD, 1)
        );
        static final VillageChestLoot BUTCHER = create(false, 1, 5,
                item(ItemID.EMERALD, 1),
                item(ItemID.PORKCHOP, 0, 3, 1, 6),
                item(WHEAT, 0, 3, 1, 6),
                item(ItemID.BEEF, 0, 3, 1, 6),
                item(ItemID.MUTTON, 0, 3, 1, 6),
                item(ItemID.COAL, 0, 3, 1, 3)
        );
        static final VillageChestLoot CARTOGRAPHER = create(true, 1, 5,
                item(ItemID.EMPTY_MAP, 0, 3, 1, 10),
                item(ItemID.PAPER, 0, 5, 1, 15),
                item(ItemID.COMPASS, 0, 1, 1, 5),
                item(ItemID.BREAD, 0, 4, 1, 15),
                item(BlockID.OAK_SAPLING, 0, 2, 1, 5)
        );
        static final VillageChestLoot DESERT_HOUSE = create(true, 3, 8,
                item(ItemID.CLAY_BALL, 1),
                item(ItemID.DYE, 2, 1, 1, 1),
                item(CACTUS, 0, 4, 1, 10),
                item(WHEAT, 0, 7, 1, 10),
                item(ItemID.BREAD, 0, 4, 1, 10),
                item(ItemID.BOOK, 1),
                item(DEADBUSH, 0, 3, 1, 2),
                item(ItemID.EMERALD, 0, 3, 1, 1)
        );
        static final VillageChestLoot FLETCHER = create(false, 1, 5,
                item(ItemID.EMERALD, 1),
                item(ItemID.ARROW, 0, 3, 1, 2),
                item(ItemID.FEATHER, 0, 3, 1, 6),
                item(ItemID.EGG, 0, 3, 1, 2),
                item(ItemID.FLINT, 0, 3, 1, 6),
                item(ItemID.STICK, 0, 3, 1, 6)
        );
        static final VillageChestLoot MASON = create(false, 1, 5,
                item(ItemID.CLAY_BALL, 0, 3, 1, 1),
                item(BlockID.FLOWER_POT, 1),
                item(STONE, 0, 1, 1, 2),
                item(STONE_BRICKS, 0, 1, 1, 2),
                item(ItemID.BREAD, 0, 4, 1, 4),
                item(ItemID.YELLOW_DYE, 1),
                item(SMOOTH_STONE, 1),
                item(ItemID.EMERALD, 1)
        );
        static final VillageChestLoot PLAINS_HOUSE = create(true, 3, 8,
                item(ItemID.GOLD_NUGGET, 0, 3, 1, 1),
                item(DANDELION, 0, 1, 1, 2),
                item(POPPY, 1),
                item(ItemID.POTATO, 0, 7, 1, 10),
                item(ItemID.BREAD, 0, 4, 1, 10),
                item(ItemID.APPLE, 0, 5, 1, 10),
                item(ItemID.BOOK, 1),
                item(ItemID.FEATHER, 1),
                item(ItemID.EMERALD, 0, 4, 1, 2),
                item(BlockID.OAK_SAPLING, 0, 2, 1, 5)
        );
        static final VillageChestLoot SAVANNA_HOUSE = create(true, 3, 8,
                item(ItemID.GOLD_NUGGET, 0, 3, 1, 1),
                item(SHORT_GRASS, 0, 1, 1, 5),
                item(TALL_GRASS, 0, 1, 1, 5),
                item(ItemID.BREAD, 0, 4, 1, 10),
                item(ItemID.WHEAT_SEEDS, 0, 5, 1, 10),
                item(ItemID.EMERALD, 0, 4, 1, 2),
                item(ACACIA_SAPLING, 0, 2, 1, 10),
                item(ItemID.SADDLE, 1),
                item(TORCH, 0, 2, 1, 1),
                item(ItemID.BUCKET, 1)
        );
        static final VillageChestLoot SHEPHERD = create(false, 1, 5,
                item(WHITE_WOOL, 0, 8, 1, 6),
                item(BlockID.BLACK_WOOL, 0, 3, 1, 3),
                item(BlockID.GRAY_WOOL, 0, 3, 1, 2),
                item(BROWN_WOOL, 0, 3, 1, 2),
                item(LIGHT_GRAY_WOOL, 0, 3, 1, 2),
                item(ItemID.EMERALD, 1),
                item(ItemID.SHEARS, 1),
                item(WHEAT, 0, 6, 1, 6)
        );
        static final VillageChestLoot SNOWY_HOUSE = create(true, 3, 8,
                item(BLUE_ICE, 1),
                item(SNOW, 0, 1, 1, 4),
                item(ItemID.POTATO, 0, 7, 1, 10),
                item(ItemID.BREAD, 0, 4, 1, 10),
                item(ItemID.BEETROOT_SEEDS, 0, 5, 1, 10),
                item(ItemID.BEETROOT_SOUP, 1),
                item(BlockID.FURNACE, 1),
                item(ItemID.EMERALD, 0, 4, 1, 1),
                item(ItemID.SNOWBALL, 0, 7, 1, 10),
                item(ItemID.COAL, 0, 4, 1, 5)
        );
        static final VillageChestLoot TAIGA_HOUSE = create(true, 3, 8,
                item(ItemID.IRON_NUGGET, 0, 5, 1, 1),
                item(FERN, 0, 1, 1, 2),
                item(LARGE_FERN, 0, 1, 1, 2),
                item(ItemID.POTATO, 0, 7, 1, 10),
                item(ItemID.BREAD, 0, 4, 1, 10),
                item(ItemID.PUMPKIN_SEEDS, 0, 5, 1, 5),
                item(ItemID.PUMPKIN_PIE, 1),
                item(ItemID.EMERALD, 0, 4, 1, 2),
                item(SPRUCE_SAPLING, 0, 5, 1, 5),
                item(ItemID.OAK_SIGN, 1, 1, 1, 1),
                item(SPRUCE_LOG, 0, 5, 1, 10)
        );
        static final VillageChestLoot TANNERY = create(true, 1, 5,
                item(ItemID.LEATHER, 0, 3, 1, 1),
                item(ItemID.LEATHER_CHESTPLATE, 0, 1, 1, 2),
                item(ItemID.LEATHER_BOOTS, 0, 1, 1, 2),
                item(ItemID.LEATHER_HELMET, 0, 1, 1, 2),
                item(ItemID.BREAD, 0, 4, 1, 5),
                item(ItemID.LEATHER_LEGGINGS, 0, 1, 1, 2),
                item(ItemID.SADDLE, 1),
                item(ItemID.EMERALD, 0, 4, 1, 1)
        );
        static final VillageChestLoot TEMPLE = create(false, 3, 8,
                item(ItemID.REDSTONE, 0, 4, 1, 2),
                item(ItemID.BREAD, 0, 4, 1, 7),
                item(ItemID.ROTTEN_FLESH, 0, 4, 1, 7),
                item(ItemID.DYE, 4, 4, 1, 1),
                item(ItemID.GOLD_INGOT, 0, 4, 1, 1),
                item(ItemID.EMERALD, 0, 4, 1, 1)
        );
        static final VillageChestLoot TOOLSMITH = create(false, 3, 8,
                item(ItemID.DIAMOND, 0, 3, 1, 1),
                item(ItemID.IRON_INGOT, 0, 5, 1, 5),
                item(ItemID.GOLD_INGOT, 0, 3, 1, 1),
                item(ItemID.BREAD, 0, 3, 1, 15),
                item(ItemID.IRON_PICKAXE, 0, 1, 1, 5),
                item(ItemID.COAL, 0, 3, 1, 1),
                item(ItemID.STICK, 0, 3, 1, 20),
                item(ItemID.IRON_SHOVEL, 0, 1, 1, 5)
        );
        static final VillageChestLoot WEAPONSMITH = create(true, 3, 8,
                item(ItemID.DIAMOND, 0, 3, 1, 3),
                item(ItemID.IRON_INGOT, 0, 5, 1, 10),
                item(ItemID.GOLD_INGOT, 0, 3, 1, 5),
                item(ItemID.BREAD, 0, 3, 1, 15),
                item(ItemID.APPLE, 0, 3, 1, 15),
                item(ItemID.IRON_PICKAXE, 0, 1, 1, 5),
                item(ItemID.IRON_SWORD, 0, 1, 1, 5),
                item(ItemID.IRON_CHESTPLATE, 0, 1, 1, 5),
                item(ItemID.IRON_HELMET, 0, 1, 1, 5),
                item(ItemID.IRON_LEGGINGS, 0, 1, 1, 5),
                item(ItemID.IRON_BOOTS, 0, 1, 1, 5),
                item(BlockID.OBSIDIAN, 0, 7, 3, 5),
                item(BlockID.OAK_SAPLING, 0, 7, 3, 5),
                item(ItemID.SADDLE, 0, 1, 1, 3),
                item(ItemID.IRON_HORSE_ARMOR, 1),
                item(ItemID.GOLDEN_HORSE_ARMOR, 1),
                item(ItemID.DIAMOND_HORSE_ARMOR, 1)
        );

        private final boolean includesBundle;

        private VillageChestLoot(boolean includesBundle) {
            this.includesBundle = includesBundle;
        }

        @Override
        public void create(Inventory inventory, RandomSourceProvider random) {
            super.create(inventory, random);
            if (includesBundle && random.nextBoundedInt(3) == 0) {
                inventory.setItem(random.nextBoundedInt(inventory.getSize()), Item.get(ItemID.BUNDLE));
            }
        }

        private static VillageChestLoot create(boolean includesBundle, int minRolls, int maxRolls, ItemEntry... entries) {
            VillageChestLoot loot = new VillageChestLoot(includesBundle);
            PoolBuilder pool = new PoolBuilder();
            for (ItemEntry entry : entries) {
                pool.register(entry);
            }
            loot.pools.put(pool.build(), new RollEntry(maxRolls, minRolls, pool.getTotalWeight()));
            return loot;
        }

        private static ItemEntry item(String id, int weight) {
            return new ItemEntry(id, 0, 1, 1, weight);
        }

        private static ItemEntry item(String id, int meta, int maxCount, int minCount, int weight) {
            return new ItemEntry(id, meta, maxCount, minCount, weight);
        }
    }
}
