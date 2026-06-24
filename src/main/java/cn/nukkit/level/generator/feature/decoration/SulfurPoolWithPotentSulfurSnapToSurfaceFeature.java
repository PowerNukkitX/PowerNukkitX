package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPotentSulfur;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockSulfur;
import cn.nukkit.block.BlockSulfurSpike;
import cn.nukkit.block.BlockWater;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.PotentSulfurState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/06/23
 */
public class SulfurPoolWithPotentSulfurSnapToSurfaceFeature extends LakeFeature {

    public static final String NAME = "minecraft:sulfur_pool_with_potent_sulfur_snap_to_surface_feature";
    private static final int PLACEMENT_ATTEMPTS = 256;
    private static final int MAX_ENVIRONMENT_SCAN_STEPS = 32;

    private static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    private static final BlockState SULFUR = BlockSulfur.PROPERTIES.getDefaultState();
    private static final BlockState WET_POTENT_SULFUR = BlockPotentSulfur.PROPERTIES.getBlockState(
            CommonBlockProperties.POTENT_SULFUR_STATE.createValue(PotentSulfurState.WET)
    );

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());
        BlockManager manager = new BlockManager(level);
        int minY = level.getMinHeight();
        int maxY = Math.min(256, level.getMaxHeight());
        boolean placed = false;

        for (int i = 0; i < PLACEMENT_ATTEMPTS; i++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int y = minY + random.nextInt(maxY - minY + 1);
            int z = (chunkZ << 4) + random.nextInt(16);

            if (!manager.getBlockIfCachedOrLoaded(x, y, z).isSolid()) {
                continue;
            }

            int surfaceY = scanUpToSurface(manager, x, y, z, maxY);
            if (surfaceY == Integer.MIN_VALUE || level.getBiomeId(x, surfaceY, z) != BiomeID.SULFUR_CAVES) {
                continue;
            }

            int lakeY = surfaceY + 1;
            if (place(manager, x, lakeY, z)) {
                placePotentSulfur(manager, x, lakeY, z);
                placed = true;
                break;
            }
        }

        if (placed) {
            queueObject(chunk, manager);
        }
    }

    @Override
    protected BlockState fluid() {
        return WATER;
    }

    @Override
    protected BlockState barrier() {
        return SULFUR;
    }

    @Override
    protected boolean canPlaceFeature(BlockManager manager, int x, int y, int z) {
        return !BlockID.SULFUR_SPIKE.equals(manager.getBlockIfCachedOrLoaded(x, y, z).getId());
    }

    private void placePotentSulfur(BlockManager manager, int x, int y, int z) {
        for (int step = 0; step <= 4; step++) {
            int scanY = y - step;
            Block block = manager.getBlockIfCachedOrLoaded(x, scanY, z);
            if (block.isSolid() && isWater(manager.getBlockIfCachedOrLoaded(x, scanY + 1, z))) {
                manager.setBlockStateAt(x, scanY, z, WET_POTENT_SULFUR);
                return;
            }
        }
    }

    private int scanUpToSurface(BlockManager manager, int x, int y, int z, int maxY) {
        for (int step = 0; step <= MAX_ENVIRONMENT_SCAN_STEPS && y + step <= maxY; step++) {
            int scanY = y + step;
            Block block = manager.getBlockIfCachedOrLoaded(x, scanY, z);
            if (block.isAir() || block instanceof BlockSulfurSpike) {
                return scanY - 1;
            }
        }
        return Integer.MIN_VALUE;
    }

    private boolean isWater(Block block) {
        return block instanceof BlockFlowingWater || BlockID.WATER.equals(block.getId()) || BlockID.FLOWING_WATER.equals(block.getId());
    }

    @Override
    public String name() {
        return NAME;
    }
}
