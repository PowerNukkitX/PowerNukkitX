package org.powernukkitx.level.generator.feature.river;

import org.powernukkitx.block.BlockSand;
import org.powernukkitx.block.BlockState;

public class SandGenerateFeature extends DiscGenerateFeature {

    public static final String NAME = "minecraft:minecraft:overworld_surface_sand_feature";

    protected static final BlockState STATE = BlockSand.PROPERTIES.getDefaultState();


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
        return 7;
    }

    @Override
    public int getRadiusY() {
        return 2;
    }

    @Override
    public int getBase() {
        return 5;
    }

    @Override
    public String name() {
        return NAME;
    }
}
