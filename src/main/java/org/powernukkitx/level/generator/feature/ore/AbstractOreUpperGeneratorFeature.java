package org.powernukkitx.level.generator.feature.ore;

import org.powernukkitx.block.BlockState;

public abstract class AbstractOreUpperGeneratorFeature extends OreGeneratorFeature {

    @Override
    public boolean canBeReplaced(BlockState state) {
        return state == STONE;
    }

}
