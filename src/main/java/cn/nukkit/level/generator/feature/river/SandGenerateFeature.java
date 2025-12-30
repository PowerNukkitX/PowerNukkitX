package cn.nukkit.level.generator.feature.river;

import cn.nukkit.block.BlockSand;
import cn.nukkit.block.BlockState;

public class SandGenerateFeature extends DiscGenerateFeature {

    public static final String NAME = "minecraft:minecraft:overworld_surface_sand_feature"; //Who messed that up? Why is it minecraft:minecraft: ??

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
