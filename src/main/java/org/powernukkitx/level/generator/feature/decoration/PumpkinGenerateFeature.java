package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.BlockPumpkin;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.object.BlockManager;

public class PumpkinGenerateFeature extends SurfaceGenerateFeature {

    private static final BlockState PUMPKIN = BlockPumpkin.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:pumpkin_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int getBase() {
        return -2000;
    }

    @Override
    public int getRandom() {
        return 2015;
    }

    @Override
    public void place(BlockManager manager, int x, int y, int z) {
        manager.setBlockStateAt(x, y, z, PUMPKIN);
    }
}
