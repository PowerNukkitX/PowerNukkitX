package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.OreGeneratorFeature;

public abstract class AbstractOreUpperGeneratorFeature extends OreGeneratorFeature {

    @Override
    public boolean canBeReplaced(BlockState state) {
        return state == STONE;
    }

}
