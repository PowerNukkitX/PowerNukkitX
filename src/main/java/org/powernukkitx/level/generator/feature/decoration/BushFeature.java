package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.BlockBush;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.object.BlockManager;

public class BushFeature extends SurfaceGenerateFeature {

    private static final BlockState STATE = BlockBush.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_bush_feature";

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, STATE);
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
