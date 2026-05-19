package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;

/**
 * @author Buddelbubi
 * @since 2026/05/10
 */
public class OverworldUnderwaterMagmaFeature extends GenerateFeature {

    public static final String NAME = "minecraft:overworld_underwater_magma_feature";

    private static final int FLOOR_SEARCH_RANGE = 5;

    private static final BlockState MAGMA = BlockMagma.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());
        BlockManager manager = new BlockManager(level);

        int count = 44 + random.nextInt(9);
        int minY = level.getMinHeight();
        int maxY = Math.min(256, level.getMaxHeight());

        for (int i = 0; i < count; i++) {
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int sampledY = minY + random.nextInt(maxY - minY + 1);
            int floorY = findFloorY(chunk, x, sampledY, z);

            if (floorY > minY && !isConnectedToSurfaceWater(chunk, x, sampledY, z)) {
                placeMagmaBlob(level, chunk, manager, x, floorY, z);
            }
        }

        queueObject(chunk, manager);
    }

    private int findFloorY(IChunk chunk, int x, int originY, int z) {
        Level level = chunk.getLevel();
        if (originY < level.getMinHeight() || originY > level.getMaxHeight() || !isWater(chunk.getBlockState(x, originY, z))) {
            return Integer.MIN_VALUE;
        }

        int minY = Math.max(level.getMinHeight(), originY - FLOOR_SEARCH_RANGE);
        for (int y = originY - 1; y >= minY; y--) {
            if (!isWater(chunk.getBlockState(x, y, z))) {
                return y;
            }
        }

        return Integer.MIN_VALUE;
    }

    private boolean isConnectedToSurfaceWater(IChunk chunk, int x, int originY, int z) {
        int surfaceY = chunk.getHeightMap(x, z);
        if (originY >= surfaceY) {
            return true;
        }

        for (int y = originY + 1; y <= surfaceY; y++) {
            if (!isWater(chunk.getBlockState(x, y, z))) {
                return false;
            }
        }

        return true;
    }

    private void placeMagmaBlob(Level level, IChunk chunk, BlockManager manager, int floorX, int floorY, int floorZ) {
        int minY = level.getMinHeight();
        int maxY = level.getMaxHeight();

        for (int x = floorX - 1; x <= floorX + 1; x++) {
            if (x < 0 || x > 15) {
                continue;
            }
            for (int y = Math.max(minY, floorY - 1); y <= Math.min(maxY, floorY + 1); y++) {
                for (int z = floorZ - 1; z <= floorZ + 1; z++) {
                    if (z < 0 || z > 15 || random.nextFloat() >= 0.5f || !canPlaceMagma(chunk, x, y, z)) {
                        continue;
                    }

                    int worldX = (chunk.getX() << 4) + x;
                    int worldY = y;
                    int worldZ = (chunk.getZ() << 4) + z;
                    manager.setBlockStateAt(worldX, worldY, worldZ, MAGMA);
                    manager.addHook(() -> level.scheduleUpdate(level.getBlock(worldX, worldY, worldZ), 1));
                }
            }
        }
    }

    private boolean canPlaceMagma(IChunk chunk, int x, int y, int z) {
        BlockState target = chunk.getBlockState(x, y, z);
        if (isWaterOrAir(target)) {
            return false;
        }

        Level level = chunk.getLevel();
        if (y - 1 < level.getMinHeight() || x - 1 < 0 || x + 1 > 15 || z - 1 < 0 || z + 1 > 15) {
            return false;
        }

        return chunk.getBlockState(x, y - 1, z).toBlock().isSolid()
                && chunk.getBlockState(x + 1, y, z).toBlock().isSolid()
                && chunk.getBlockState(x - 1, y, z).toBlock().isSolid()
                && chunk.getBlockState(x, y, z + 1).toBlock().isSolid()
                && chunk.getBlockState(x, y, z - 1).toBlock().isSolid();
    }

    private boolean isWater(BlockState state) {
        return state.toBlock() instanceof BlockFlowingWater;
    }

    private boolean isWaterOrAir(BlockState state) {
        Block block = state.toBlock();
        return block instanceof BlockFlowingWater || block.isAir();
    }

    @Override
    public String name() {
        return NAME;
    }
}
