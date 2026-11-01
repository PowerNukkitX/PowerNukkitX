package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockRedShrub;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.generator.object.BlockManager;

/**
 * Scatters red shrubs
 *
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/11/01
 */
public class ScatterRedShrubBushFeature extends SurfaceGenerateFeature {

    private static final BlockState STATE = BlockRedShrub.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_red_shrub_bush_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, STATE);
    }

    @Override
    public boolean isSupportValid(Block support) {
        return super.isSupportValid(support)
                && support.getLevel().getBiomeId(
                        support.getFloorX(),
                        support.getFloorY(),
                        support.getFloorZ()
                ) == BiomeID.DAPPLED_FOREST;
    }

    @Override
    public int getBase() {
        return 8;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }
}
