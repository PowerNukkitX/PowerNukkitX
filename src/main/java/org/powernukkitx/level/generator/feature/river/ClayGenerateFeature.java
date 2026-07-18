package org.powernukkitx.level.generator.feature.river;

import org.powernukkitx.block.BlockClay;
import org.powernukkitx.block.BlockState;

public class ClayGenerateFeature extends DiscGenerateFeature {

    public static final String NAME = "minecraft:overworld_surface_clay_feature";

    protected static final BlockState STATE = BlockClay.PROPERTIES.getDefaultState();

    @Override
    public BlockState getSourceBlock() {
        return STATE;
    }

    @Override
    public int getMinRadius() {
        return 1;
    }

    @Override
    public int getMaxRadius() {
        return 3;
    }

    @Override
    public int getRadiusY() {
        return 1;
    }

    @Override
    public int getBase() {
        return 1;
    }

    @Override
    public String name() {
        return NAME;
    }
}
