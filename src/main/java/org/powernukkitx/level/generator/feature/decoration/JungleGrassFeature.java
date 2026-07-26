package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.BlockFern;
import org.powernukkitx.block.BlockShortGrass;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.object.BlockManager;

public class JungleGrassFeature extends SurfaceGenerateFeature {

    private static final BlockState SHORT_GRASS = BlockShortGrass.PROPERTIES.getDefaultState();
    private static final BlockState FERN = BlockFern.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:jungle_tall_grass_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, random.nextInt(7) == 0 ? FERN : SHORT_GRASS);
    }

    @Override
    public int getBase() {
        return 100;
    }

    @Override
    public int getRandom() {
        return 10;
    }

    @Override
    public String name() {
        return NAME;
    }
}
