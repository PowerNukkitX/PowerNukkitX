package org.powernukkitx.level.generator.object.structures.jigsaw.village;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockChest;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.block.BlockJigsaw;
import org.powernukkitx.block.BlockSnow;
import org.powernukkitx.block.BlockSnowLayer;
import org.powernukkitx.block.BlockUnknown;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.RandomizableContainer;
import org.powernukkitx.level.generator.object.structures.StructureHelper;
import org.powernukkitx.level.generator.object.structures.jigsaw.JigsawStructure;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.holder.NormalObjectHolder;
import org.powernukkitx.level.generator.noise.minecraft.noise.NormalNoise;
import org.powernukkitx.level.structure.PNXStructure;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeSurfaceMaterialAdjustmentData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeSurfaceMaterialData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.powernukkitx.block.BlockID.*;

/**
 * Villages for PowerNukkitX
 * @author Buddelbubi
 */
public abstract class VillageStructure extends JigsawStructure {

    private static final int BEARD_KERNEL_RADIUS = 12;
    private static final int BEARD_KERNEL_SIZE = BEARD_KERNEL_RADIUS * 2;
    private static final double BEARD_THRESHOLD = 0.03;
    private static final double[] BEARD_KERNEL = createBeardKernel();
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
        helper.applySubChunkUpdate();

        placedBlocks.stream()
            .filter(BlockUnknown.class::isInstance)
            .forEach(block -> level.setBlock(block, BlockAir.STATE.toBlock(block), true, true));

        int jigsawCount = 0;
        double jigsawSumX = 0, jigsawSumZ = 0;
        for (Block block : placedBlocks) {
            if (!(block instanceof BlockJigsaw)) {
                continue;
            }
            level.setBlock(block, BlockAir.STATE.toBlock(block), true, true);
            helper.setBlockStateAt(
                    block.getFloorX() - helper.getOrigin().getX(),
                    block.getFloorY() - helper.getOrigin().getY(),
                    block.getFloorZ() - helper.getOrigin().getZ(),
                    BlockAir.STATE
            );
            int spawnX = block.getFloorX();
            int spawnZ = block.getFloorZ();
            int safeY = findSafeSpawnY(level, spawnX, block.getFloorY(), spawnZ, 2);
            Entity villager = Entity.createEntity(
                Entity.VILLAGER_V2,
                new Position(spawnX + 0.5, safeY, spawnZ + 0.5, level)
            );
            if (villager != null) {
                villager.spawnToAll();
            }
            jigsawSumX += spawnX;
            jigsawSumZ += spawnZ;
            jigsawCount++;
        }

        if (jigsawCount > 0) {
            int centerX = (int) (jigsawSumX / jigsawCount);
            int centerZ = (int) (jigsawSumZ / jigsawCount);
            int baseY = level.getHighestBlockAt(centerX, centerZ) + 1;
            int golemY = findSafeSpawnY(level, centerX, baseY, centerZ, 3);
            Entity golem = Entity.createEntity(EntityID.IRON_GOLEM,
                new Position(centerX + 0.5, golemY, centerZ + 0.5, level));
            if (golem != null) {
                golem.spawnToAll();
            }
        }

        helper.applySubChunkUpdate();
    }

    @Override
    protected void postProcessStructure(StructureHelper helper, List<BoundingBox> occupiedBoxes) {
        applyBiomeSurfaceBeardification(helper, occupiedBoxes);
        postProcessStructure(helper);
    }

    @Override
    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        liftPieceAboveWater(blockManager, jigsaws);
        if (isDecorPiece(structureName)) {
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
            registerVillageChestLoot(structureName, blockManager);
            return;
        }
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

    @Override
    protected boolean strictlyIntersects(BoundingBox first, BoundingBox second) {
        return first.x1 >= second.x0 && first.x0 <= second.x1
                && first.z1 >= second.z0 && first.z0 <= second.z1;
    }

    protected int getLampHeightOffset(String structureName) {
        return 0;
    }

    protected boolean isDecorPiece(String structureName) {
        return structureName.contains("lamp") || structureName.contains("_decoration_");
    }

    @Override
    protected boolean appliesTerrainAdaptation(String structureName) {
        return !isDecorPiece(structureName)
                && !structureName.contains("/streets/")
                && !structureName.contains("/terminators/");
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
            level.getOrGenerateChunk(x >> 4, z >> 4);

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
        Map<Long, Integer> columnHeights = new HashMap<>();
        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockJigsaw) {
                blockManager.unsetBlockStateAt(block);
                continue;
            }
            level.getOrGenerateChunk(block.getChunkX(), block.getChunkZ());
            int height = getPlacementY(level, block.getFloorX(), block.getFloorZ()) - 1;
            columnHeights.put(columnKey(block.getFloorX(), block.getFloorZ()), height);
            blockManager.unsetBlockStateAt(block);
            if (isStreet(block)) {
                if(!blockManager.isCached(new BlockVector3(block.getFloorX(), height+1, block.getFloorZ()))) {
                    blockManager.setBlockStateAt(block.getFloorX(), height + 1, block.getFloorZ(), BlockAir.STATE);
                    blockManager.setBlockStateAt(block.getFloorX(), height + 2, block.getFloorZ(), BlockAir.STATE);
                }
                blockManager.setBlockStateAt(block.getFloorX(), height, block.getFloorZ(), block.getBlockState());
            }
        }

        for (PNXStructure.Jigsaw jigsaw : jigsaws) {
            Integer height = columnHeights.get(columnKey(jigsaw.x, jigsaw.z));
            if (height == null) {
                level.getOrGenerateChunk(jigsaw.x >> 4, jigsaw.z >> 4);
                height = getPlacementY(level, jigsaw.x, jigsaw.z) - 1;
            }
            jigsaw.y = height + 1;
        }
    }

    protected long columnKey(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    private void applyBiomeSurfaceBeardification(StructureHelper helper, List<BoundingBox> occupiedBoxes) {
        Level level = helper.getLevel();
        BlockVector3 origin = helper.getOrigin();
        int minHeight = helper.getMinHeight();
        int maxHeight = helper.getMaxHeight() - 1;

        for (BoundingBox relativeBox : occupiedBoxes) {
            BoundingBox box = relativeBox.moved(origin.getX(), origin.getY(), origin.getZ());
            int minX = box.x0 - BEARD_KERNEL_RADIUS;
            int maxX = box.x1 + BEARD_KERNEL_RADIUS;
            int minY = Math.max(minHeight, box.y0 - BEARD_KERNEL_RADIUS);
            int maxY = Math.min(maxHeight, box.y1 + BEARD_KERNEL_RADIUS);
            int minZ = box.z0 - BEARD_KERNEL_RADIUS;
            int maxZ = box.z1 + BEARD_KERNEL_RADIUS;

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int dx = Math.max(0, Math.max(box.x0 - x, x - box.x1));
                    int dz = Math.max(0, Math.max(box.z0 - z, z - box.z1));
                    if (dx >= BEARD_KERNEL_RADIUS || dz >= BEARD_KERNEL_RADIUS) {
                        continue;
                    }

                    SurfaceMaterials materials = getBiomeSurfaceMaterials(level, x, box.y0, z);
                    if (materials == null) {
                        continue;
                    }

                    int highestFilledY = Integer.MIN_VALUE;
                    for (int y = minY; y <= maxY; y++) {
                        BlockVector3 position = new BlockVector3(x, y, z);
                        if (helper.isCached(position)) {
                            continue;
                        }

                        int dy = Math.max(0, Math.max(box.y0 - y, y - box.y1));
                        if (dy >= BEARD_KERNEL_RADIUS) {
                            continue;
                        }

                        double contribution = getBeardContribution(dx, dy, dz, y - box.y0) * 0.8;
                        if (contribution > BEARD_THRESHOLD) {
                            Block current = level.getBlock(x, y, z);
                            if (current.canBeReplaced() || !current.isSolid()) {
                                helper.setBlockStateAt(
                                        x - origin.getX(),
                                        y - origin.getY(),
                                        z - origin.getZ(),
                                        materials.mid()
                                );
                                highestFilledY = Math.max(highestFilledY, y);
                            }
                        }
                    }

                    if (highestFilledY != Integer.MIN_VALUE) {
                        helper.setBlockStateAt(
                                x - origin.getX(),
                                highestFilledY - origin.getY(),
                                z - origin.getZ(),
                                materials.top()
                        );
                    }
                }
            }
        }
    }

    private SurfaceMaterials getBiomeSurfaceMaterials(Level level, int x, int y, int z) {
        var biomeDefinition = Registries.BIOME.get(level.getBiomeId(x, y, z)).second();
        BiomeDefinitionChunkGenData chunkGenData = biomeDefinition == null ? null : biomeDefinition.getChunkGenData();
        var surfaceBuilder = chunkGenData == null ? null : chunkGenData.getSurfaceBuilderData();
        BiomeSurfaceMaterialData surface = surfaceBuilder == null ? null : surfaceBuilder.getSurfaceMaterial();
        if (surface == null || surface.getTopBlock() == null || surface.getMidBlock() == null) {
            return null;
        }

        int topRuntimeId = surface.getTopBlock().getRuntimeId();
        int midRuntimeId = surface.getMidBlock().getRuntimeId();
        BiomeSurfaceMaterialAdjustmentData adjustment = chunkGenData.getSurfaceMaterialAdjustment();
        if (adjustment != null && level.getGeneratorObjectHolder() instanceof NormalObjectHolder holder) {
            NormalNoise noise = holder.getSurfaceHolder().getNoise();
            float value = noise.getValue(x, 0, z);
            for (var element : adjustment.getBiomeElements()) {
                if (value < element.getNoiseUpperBound() && value > element.getNoiseLowerBound()) {
                    int adjustedTop = element.getAdjustedMaterials().getTopBlock().getRuntimeId();
                    int adjustedMid = element.getAdjustedMaterials().getMidBlock().getRuntimeId();
                    if (adjustedTop != -1) {
                        topRuntimeId = adjustedTop;
                    }
                    if (adjustedMid != -1) {
                        midRuntimeId = adjustedMid;
                    }
                }
            }
        }
        return new SurfaceMaterials(Registries.BLOCKSTATE.get(topRuntimeId), Registries.BLOCKSTATE.get(midRuntimeId));
    }

    private static double getBeardContribution(int dx, int dy, int dz, int yToGround) {
        int xi = dx + BEARD_KERNEL_RADIUS;
        int yi = dy + BEARD_KERNEL_RADIUS;
        int zi = dz + BEARD_KERNEL_RADIUS;
        if (!isInKernelRange(xi) || !isInKernelRange(yi) || !isInKernelRange(zi)) {
            return 0.0;
        }

        double dyWithOffset = yToGround + 0.5;
        double distanceSqr = dx * (double) dx + dyWithOffset * dyWithOffset + dz * (double) dz;
        double value = -dyWithOffset / Math.sqrt(distanceSqr / 2.0) / 2.0;
        return value * BEARD_KERNEL[zi * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE + xi * BEARD_KERNEL_SIZE + yi];
    }

    private static boolean isInKernelRange(int index) {
        return index >= 0 && index < BEARD_KERNEL_SIZE;
    }

    private static double[] createBeardKernel() {
        double[] kernel = new double[BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE];
        for (int zi = 0; zi < BEARD_KERNEL_SIZE; zi++) {
            for (int xi = 0; xi < BEARD_KERNEL_SIZE; xi++) {
                for (int yi = 0; yi < BEARD_KERNEL_SIZE; yi++) {
                    int dx = xi - BEARD_KERNEL_RADIUS;
                    double dy = yi - BEARD_KERNEL_RADIUS + 0.5;
                    int dz = zi - BEARD_KERNEL_RADIUS;
                    double distanceSqr = dx * (double) dx + dy * dy + dz * (double) dz;
                    kernel[zi * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE + xi * BEARD_KERNEL_SIZE + yi] = Math.exp(-distanceSqr / 16.0);
                }
            }
        }
        return kernel;
    }

    private record SurfaceMaterials(BlockState top, BlockState mid) {
    }

    protected int getTerrainY(Level level, int x, int z) {
        int height = level.getHeightMap(x, z);
        while (height > level.getMinHeight()
            && (isReplaceableTerrainCover(level.getBlock(x, height, z))
            || level.getBlock(x, height, z).canBeReplaced()
            || level.getBlock(x, height, z).isTransparent())) {
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

    private int findSafeSpawnY(Level level, int x, int startY, int z, int entityHeight) {
        outer:
        for (int y = startY; y <= startY + 16; y++) {
            for (int dy = 0; dy < entityHeight; dy++) {
                if (level.getBlock(x, y + dy, z).isSolid()) continue outer;
            }
            return y;
        }
        return startY;
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
