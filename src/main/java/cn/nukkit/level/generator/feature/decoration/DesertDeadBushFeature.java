package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockDeadbush;
import cn.nukkit.block.BlockState;

public class DesertDeadBushFeature extends GroupedDiscFeature {

    protected final static BlockState DEAD_BUSH = BlockDeadbush.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:desert_after_surface_dead_bush_feature_rules";

    @Override
    public BlockState getSourceBlock() {
        return DEAD_BUSH;
    }

    @Override
    public int getMinRadius() {
        return 3;
    }

    @Override
    public int getMaxRadius() {
        return 4;
    }

    @Override
    public double getProbability() {
        return 0.1f;
    }

    @Override
    public int getBase() {
        return -19;
    }

    @Override
    public int getRandom() {
        return 20;
    }

    @Override
    public String name() {
        return NAME;
    }
}
