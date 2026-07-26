package org.powernukkitx.level.generator.feature.river;

import org.powernukkitx.block.BlockGravel;
import org.powernukkitx.block.BlockState;

public class GravelGenerateFeature extends DiscGenerateFeature {

    public static final String NAME = "minecraft:overworld_surface_gravel_feature";

    protected static final BlockState STATE = BlockGravel.PROPERTIES.getDefaultState();

    @Override
    public BlockState getSourceBlock() {
        return STATE;
    }

    @Override
    public int getMinRadius() {
        return 2;
    }

    @Override
    public int getMaxRadius() {
        return 5;
    }

    @Override
    public int getRadiusY() {
        return 2;
    }

    @Override
    public int getBase() {
        return 3;
    }

    @Override
    public String name() {
        return NAME;
    }
}
