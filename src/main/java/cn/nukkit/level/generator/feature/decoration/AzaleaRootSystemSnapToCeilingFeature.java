package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockDirtWithRoots;
import cn.nukkit.block.BlockHangingRoots;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectAzaleaTree;
import cn.nukkit.math.Vector3;

public class AzaleaRootSystemSnapToCeilingFeature extends GenerateFeature {
    public static final String NAME = "minecraft:azalea_root_system_snap_to_ceiling_feature";

    private static final BlockState ROOTED_DIRT = BlockDirtWithRoots.PROPERTIES.getDefaultState();
    private static final BlockState HANGING_ROOTS = BlockHangingRoots.PROPERTIES.getDefaultState();

    private static final int REQUIRED_VERTICAL_SPACE_FOR_TREE = 4;
    private static final int ROOT_COLUMN_MAX_HEIGHT = 96;
    private static final int MIN_TREE_XZ_DISTANCE = 5;
    private static final int ROOT_RADIUS = 3;
    private static final int ROOT_PLACEMENT_ATTEMPTS = 18;
    private static final int HANGING_ROOT_RADIUS = 5;
    private static final int HANGING_ROOT_VERTICAL_SPAN = 4;
    private static final int HANGING_ROOT_PLACEMENT_ATTEMPTS = 32;

    @Override
    public void apply(ChunkGenerateContext context) {
        var chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());

        SimplexF noise = ((NormalObjectHolder) level.getGeneratorObjectHolder()).getFeatureHolder().getMossSnapToCeiling();
        BlockManager manager = new BlockManager(level);
        ObjectAzaleaTree azaleaTree = new ObjectAzaleaTree();

        for (int x = 0; x < 16; x++) {
            int worldX = (chunkX << 4) + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = (chunkZ << 4) + z;
                if (noise.noise2D(worldX * 0.25f, worldZ * 0.25f, true) <= 0.6f) {
                    continue;
                }

                int surfaceY = chunk.getHeightMap(x, z);
                for (int y = surfaceY; y > level.getMinHeight() + 8; y--) {
                    if (chunk.getSection(y >> 4).getBiomeId(x, y & 0x0f, z) != BiomeID.LUSH_CAVES) {
                        continue;
                    }

                    if (chunk.getBlockState(x, y, z) != BlockAir.STATE) {
                        continue;
                    }

                    if (!isCeilingAnchor(manager, worldX, y, worldZ)) {
                        continue;
                    }

                    if (placeDirtAndTree(manager, azaleaTree, worldX, y, worldZ, surfaceY)) {
                        placeHangingRoots(manager, worldX, y, worldZ);
                        break;
                    }
                }
            }
        }

        queueObject(chunk, manager);
    }

    private boolean placeDirtAndTree(BlockManager manager, ObjectAzaleaTree tree, int originX, int originY, int originZ, int surfaceY) {
        int chosenTreeY = -1;
        int maxTreeY = Math.min(originY + ROOT_COLUMN_MAX_HEIGHT, surfaceY);
        for (int treeY = maxTreeY; treeY > originY; treeY--) {
            if (allowedTreePosition(manager, originX, treeY, originZ)
                    && spaceForTree(manager, originX, treeY, originZ)
                    && !hasNearbyTree(manager, originX, treeY, originZ, MIN_TREE_XZ_DISTANCE)) {
                chosenTreeY = treeY;
                break;
            }
        }

        if (chosenTreeY < 0) {
            return false;
        }

        int belowY = chosenTreeY - 1;
        String belowId = manager.getBlockIdIfCachedOrLoaded(originX, belowY, originZ);
        if (BlockID.LAVA.equals(belowId) || BlockID.FLOWING_LAVA.equals(belowId) || !manager.getBlockIfCachedOrLoaded(originX, belowY, originZ).isSolid()) {
            return false;
        }

        if (tree.generate(manager, random, new Vector3(originX, chosenTreeY, originZ))) {
            placeRootedDirtColumnDownward(manager, originX, originZ, chosenTreeY - 1, originY);
            return true;
        }

        return false;
    }

    private void placeRootedDirtColumnDownward(BlockManager manager, int originX, int originZ, int startY, int endY) {
        for (int y = startY; y >= endY; y--) {
            placeRootedDirtLayer(manager, originX, y, originZ);
        }
    }

    private void placeRootedDirtLayer(BlockManager manager, int originX, int y, int originZ) {
        for (int i = 0; i < ROOT_PLACEMENT_ATTEMPTS; i++) {
            int x = originX + random.nextInt(ROOT_RADIUS) - random.nextInt(ROOT_RADIUS);
            int z = originZ + random.nextInt(ROOT_RADIUS) - random.nextInt(ROOT_RADIUS);
            String id = manager.getBlockIdIfCachedOrLoaded(x, y, z);
            if (isRootReplaceable(id)) {
                manager.setBlockStateAt(x, y, z, ROOTED_DIRT);
            }
        }
    }

    private void placeHangingRoots(BlockManager manager, int originX, int originY, int originZ) {
        for (int i = 0; i < HANGING_ROOT_PLACEMENT_ATTEMPTS; i++) {
            int x = originX + random.nextInt(HANGING_ROOT_RADIUS) - random.nextInt(HANGING_ROOT_RADIUS);
            int y = originY + random.nextInt(HANGING_ROOT_VERTICAL_SPAN) - random.nextInt(HANGING_ROOT_VERTICAL_SPAN);
            int z = originZ + random.nextInt(HANGING_ROOT_RADIUS) - random.nextInt(HANGING_ROOT_RADIUS);

            if (!BlockID.AIR.equals(manager.getBlockIdIfCachedOrLoaded(x, y, z))) {
                continue;
            }
            if (!manager.getBlockIfCachedOrLoaded(x, y + 1, z).isSolid()) {
                continue;
            }
            manager.setBlockStateAt(x, y, z, HANGING_ROOTS);
        }
    }

    private static boolean isCeilingAnchor(BlockManager manager, int x, int y, int z) {
        return manager.getBlockIfCachedOrLoaded(x, y + 1, z).isSolid();
    }

    private static boolean allowedTreePosition(BlockManager manager, int x, int y, int z) {
        return BlockID.AIR.equals(manager.getBlockIdIfCachedOrLoaded(x, y, z));
    }

    private static boolean spaceForTree(BlockManager manager, int x, int y, int z) {
        for (int i = 1; i <= REQUIRED_VERTICAL_SPACE_FOR_TREE; i++) {
            if (!BlockID.AIR.equals(manager.getBlockIdIfCachedOrLoaded(x, y + i, z))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isRootReplaceable(String id) {
        return BlockID.STONE.equals(id)
                || BlockID.DEEPSLATE.equals(id)
                || BlockID.TUFF.equals(id)
                || BlockID.DIRT.equals(id)
                || BlockID.CLAY.equals(id)
                || BlockID.MOSS_BLOCK.equals(id)
                || BlockID.DIRT_WITH_ROOTS.equals(id);
    }

    private static boolean hasNearbyTree(BlockManager manager, int centerX, int centerY, int centerZ, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                for (int dy = -2; dy <= 10; dy++) {
                    String id = manager.getBlockIdIfCachedOrLoaded(centerX + dx, centerY + dy, centerZ + dz);
                    if (isTreeBlockId(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isTreeBlockId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        String lower = id.toLowerCase();
        return lower.contains("log")
                || lower.contains("wood")
                || lower.contains("leaves")
                || lower.contains("azalea");
    }

    @Override
    public String name() {
        return NAME;
    }
}
