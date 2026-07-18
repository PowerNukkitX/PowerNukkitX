package org.powernukkitx.level.generator.feature.decoration;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockSulfur;
import org.powernukkitx.block.BlockTuff;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.structure.AbstractStructure;
import org.powernukkitx.level.structure.JeStructure;
import org.powernukkitx.level.structure.PNXStructure;
import org.powernukkitx.level.structure.Structure;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.registry.Registries;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SulfurSpringTrailToSurfaceSnapToCeilingFeature extends GenerateFeature {
    public static final String NAME = "minecraft:sulfur_spring_trail_to_surface_snap_to_ceiling_feature";

    private static final int REQUIRED_VERTICAL_SPACE_FOR_TREE = 5;
    private static final int LEVEL_TEST_DISTANCE = 8;
    private static final int MAX_LEVEL_DEVIATION = 2;
    private static final int ROOT_RADIUS = 3;
    private static final int ROOT_PLACEMENT_ATTEMPTS = 20;
    private static final int ROOT_COLUMN_MAX_HEIGHT = 184;
    private static final int HANGING_ROOT_PLACEMENT_ATTEMPTS = 1;
    private static final int HANGING_ROOT_RADIUS = 1;
    private static final int HANGING_ROOT_VERTICAL_SPAN = 1;
    private static final int ALLOWED_VERTICAL_WATER_FOR_TREE = 1;
    private static final int MAX_CEILING_SCAN_STEPS = 12;
    private static final int TEMPLATE_Y_OFFSET = -7;

    private static final BlockState SULFUR = BlockSulfur.PROPERTIES.getDefaultState();
    private static final BlockState TUFF = BlockTuff.PROPERTIES.getDefaultState();
    private static final Map<String, AbstractStructure> STRUCTURE_CACHE = new HashMap<>();

    private static final SpringVariant[] SPRING_VARIANTS = new SpringVariant[]{
            new SpringVariant(200, 64, 7,
                    "spring/sulfur_spring_small_1",
                    "spring/sulfur_spring_small_2",
                    "spring/sulfur_spring_small_3",
                    "spring/sulfur_spring_small_4"),
            new SpringVariant(90, 80, 8,
                    "spring/sulfur_spring_medium_1",
                    "spring/sulfur_spring_medium_2",
                    "spring/sulfur_spring_medium_3"),
            new SpringVariant(20, 96, 9,
                    "spring/sulfur_spring_large_1",
                    "spring/sulfur_spring_large_2"),
            new SpringVariant(5, 128, 10,
                    "spring/sulfur_spring_extra_large_1")
    };

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());

        BlockManager manager = new BlockManager(level);
        int minY = level.getMinHeight();
        int maxY = Math.min(256, level.getMaxHeight() - 1);
        int count = random.nextInt(1, 2);
        boolean placed = false;

        for (int i = 0; i < count; i++) {
            int x = (chunkX << 4) + random.nextBoundedInt(15);
            int z = (chunkZ << 4) + random.nextBoundedInt(15);
            int y = random.nextInt(minY, maxY);

            int originY = findCeilingAnchor(manager, x, y, z, minY, maxY);
            if (originY < minY || originY > maxY) {
                continue;
            }
            if (placeRootedSpring(manager, x, originY, z, minY, maxY)) {
                placed = true;
            }
        }

        if (placed) {
            queueObject(chunk, manager);
        }
    }

    private int findCeilingAnchor(BlockManager manager, int x, int y, int z, int minY, int maxY) {
        for (int step = 0; step <= MAX_CEILING_SCAN_STEPS && y + step <= maxY; step++) {
            int scanY = y + step;
            Block block = manager.getBlockIfCachedOrLoaded(x, scanY, z);
            if (block.isSolid()) {
                int anchorY = scanY - 1;
                if (anchorY < minY) {
                    return Integer.MIN_VALUE;
                }
                return manager.getBlockIfCachedOrLoaded(x, anchorY, z).isAir() ? anchorY : Integer.MIN_VALUE;
            }
            if (!block.isAir()) {
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }

    private boolean placeRootedSpring(BlockManager manager, int originX, int originY, int originZ, int minY, int maxY) {
        if (!manager.getBlockIfCachedOrLoaded(originX, originY, originZ).isAir()) {
            return false;
        }

        int chosenY = findAllowedTreePosition(manager, originX, originY, originZ, minY, maxY);
        if (chosenY == Integer.MIN_VALUE) {
            return false;
        }

        placeSulfurSpring(manager, originX, chosenY, originZ, minY);
        placeRootColumn(manager, originX, originZ, originY, chosenY);
        placeHangingRoots(manager, originX, originY, originZ);
        return true;
    }

    private int findAllowedTreePosition(BlockManager manager, int x, int originY, int z, int minY, int maxY) {
        int endY = Math.min(originY + ROOT_COLUMN_MAX_HEIGHT, maxY);
        for (int y = Math.max(originY, minY + 1); y <= endY; y++) {
            if (allowedTreePosition(manager, x, y, z)
                    && hasRequiredVerticalSpace(manager, x, y, z)
                    && passesLevelTest(manager, x, y, z)
                    && hasSolidNonLavaBelow(manager, x, y, z)) {
                return y;
            }
        }
        return Integer.MIN_VALUE;
    }

    private void placeRootColumn(BlockManager manager, int originX, int originZ, int startY, int endY) {
        int min = Math.min(startY, endY);
        int max = Math.max(startY, endY);
        for (int y = min; y <= max; y++) {
            for (int i = 0; i < ROOT_PLACEMENT_ATTEMPTS; i++) {
                int x = originX + random.nextInt(ROOT_RADIUS) - random.nextInt(ROOT_RADIUS);
                int z = originZ + random.nextInt(ROOT_RADIUS) - random.nextInt(ROOT_RADIUS);
                if (isRootReplaceable(manager.getBlockIdIfCachedOrLoaded(x, y, z))) {
                    manager.setBlockStateAt(x, y, z, SULFUR);
                }
            }
        }
    }

    private void placeHangingRoots(BlockManager manager, int originX, int originY, int originZ) {
        for (int i = 0; i < HANGING_ROOT_PLACEMENT_ATTEMPTS; i++) {
            int x = originX + random.nextInt(HANGING_ROOT_RADIUS) - random.nextInt(HANGING_ROOT_RADIUS);
            int y = originY + random.nextInt(HANGING_ROOT_VERTICAL_SPAN) - random.nextInt(HANGING_ROOT_VERTICAL_SPAN);
            int z = originZ + random.nextInt(HANGING_ROOT_RADIUS) - random.nextInt(HANGING_ROOT_RADIUS);
            if (manager.getBlockIfCachedOrLoaded(x, y, z).isAir() && manager.getBlockIfCachedOrLoaded(x, y + 1, z).isSolid()) {
                manager.setBlockStateAt(x, y, z, SULFUR);
            }
        }
    }

    private void placeSulfurSpring(BlockManager manager, int originX, int originY, int originZ, int minY) {
        SpringVariant variant = pickVariant();
        placeTuffCover(manager, originX, originY, originZ, minY, variant.tuffCount(), variant.tuffSpread());
        int templateY = originY + TEMPLATE_Y_OFFSET;
        if (templateY >= minY) {
            placeTemplate(manager, originX, templateY, originZ, variant.pickTemplate(random.nextInt(variant.templates().length - 1)));
        }
    }

    private SpringVariant pickVariant() {
        int totalWeight = 0;
        for (SpringVariant variant : SPRING_VARIANTS) {
            totalWeight += variant.weight();
        }

        int value = random.nextInt(totalWeight - 1);
        for (SpringVariant variant : SPRING_VARIANTS) {
            value -= variant.weight();
            if (value < 0) {
                return variant;
            }
        }
        return SPRING_VARIANTS[0];
    }

    private void placeTuffCover(BlockManager manager, int originX, int originY, int originZ, int minY, int count, int spread) {
        for (int i = 0; i < count; i++) {
            int x = originX + triangle(spread);
            int y = originY + triangle(3);
            int z = originZ + triangle(spread);
            int targetY = scanDownToSolid(manager, x, y, z, minY, 4);
            if (targetY != Integer.MIN_VALUE && manager.getBlockIfCachedOrLoaded(x, targetY, z).isSolid()) {
                manager.setBlockStateAt(x, targetY, z, TUFF);
            }
        }
    }

    private int scanDownToSolid(BlockManager manager, int x, int y, int z, int minY, int maxSteps) {
        for (int step = 0; step <= maxSteps && y - step >= minY; step++) {
            int scanY = y - step;
            if (manager.getBlockIfCachedOrLoaded(x, scanY, z).isSolid()) {
                return scanY;
            }
        }
        return Integer.MIN_VALUE;
    }

    private void placeTemplate(BlockManager manager, int originX, int originY, int originZ, String templateName) {
        AbstractStructure structure = Registries.STRUCTURE.get(templateName);
        if (structure == null) {
            return;
        }

        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length - 1)];
        AbstractStructure rotated = structure.rotate(rotation);
        BlockVector3 size = getSize(rotated);
        if (size == null) {
            return;
        }

        int placeX = originX - (size.getX() / 2);
        int placeZ = originZ - (size.getZ() / 2);
        rotated.preparePlace(new Position(placeX, originY, placeZ, manager.getLevel()), manager);
    }

    private BlockVector3 getSize(AbstractStructure structure) {
        if (structure instanceof JeStructure je) {
            return new BlockVector3(je.getSizeX(), je.getSizeY(), je.getSizeZ());
        }
        if (structure instanceof PNXStructure pnx) {
            return pnx.getBounds();
        }
        if (structure instanceof Structure bedrock) {
            return new BlockVector3(bedrock.getSizeX(), bedrock.getSizeY(), bedrock.getSizeZ());
        }
        return null;
    }

    private int triangle(int spread) {
        return random.nextInt(spread) - random.nextInt(spread);
    }

    private boolean allowedTreePosition(BlockManager manager, int x, int y, int z) {
        return manager.getBlockIfCachedOrLoaded(x, y, z).isAir();
    }

    private boolean hasRequiredVerticalSpace(BlockManager manager, int x, int y, int z) {
        int water = 0;
        for (int i = 1; i <= REQUIRED_VERTICAL_SPACE_FOR_TREE; i++) {
            Block block = manager.getBlockIfCachedOrLoaded(x, y + i, z);
            if (block.isAir()) {
                continue;
            }
            if (isWater(block) && ++water <= ALLOWED_VERTICAL_WATER_FOR_TREE) {
                continue;
            }
            return false;
        }
        return true;
    }

    private boolean passesLevelTest(BlockManager manager, int x, int y, int z) {
        int baseY = y - 1;
        return Math.abs(findTopSolidY(manager, x + LEVEL_TEST_DISTANCE, baseY, z) - baseY) <= MAX_LEVEL_DEVIATION
                && Math.abs(findTopSolidY(manager, x - LEVEL_TEST_DISTANCE, baseY, z) - baseY) <= MAX_LEVEL_DEVIATION
                && Math.abs(findTopSolidY(manager, x, baseY, z + LEVEL_TEST_DISTANCE) - baseY) <= MAX_LEVEL_DEVIATION
                && Math.abs(findTopSolidY(manager, x, baseY, z - LEVEL_TEST_DISTANCE) - baseY) <= MAX_LEVEL_DEVIATION;
    }

    private int findTopSolidY(BlockManager manager, int x, int startY, int z) {
        for (int offset = 0; offset <= MAX_LEVEL_DEVIATION; offset++) {
            if (manager.getBlockIfCachedOrLoaded(x, startY + offset, z).isSolid()) {
                return startY + offset;
            }
            if (manager.getBlockIfCachedOrLoaded(x, startY - offset, z).isSolid()) {
                return startY - offset;
            }
        }
        return Integer.MIN_VALUE / 2;
    }

    private boolean hasSolidNonLavaBelow(BlockManager manager, int x, int y, int z) {
        Block below = manager.getBlockIfCachedOrLoaded(x, y - 1, z);
        return below.isSolid() && !BlockID.LAVA.equals(below.getId()) && !BlockID.FLOWING_LAVA.equals(below.getId());
    }

    private boolean isRootReplaceable(String id) {
        return BlockID.STONE.equals(id)
                || BlockID.DEEPSLATE.equals(id)
                || BlockID.TUFF.equals(id)
                || BlockID.DIRT.equals(id)
                || BlockID.CLAY.equals(id)
                || BlockID.MOSS_BLOCK.equals(id)
                || BlockID.DIRT_WITH_ROOTS.equals(id)
                || BlockID.SULFUR.equals(id)
                || BlockID.CINNABAR.equals(id);
    }

    private boolean isWater(Block block) {
        return block instanceof BlockWater || BlockID.WATER.equals(block.getId()) || BlockID.FLOWING_WATER.equals(block.getId());
    }

    @Override
    public String name() {
        return NAME;
    }

    private record SpringVariant(int weight, int tuffCount, int tuffSpread, String... templates) {
        private String pickTemplate(int index) {
            return templates[index];
        }
    }
}
