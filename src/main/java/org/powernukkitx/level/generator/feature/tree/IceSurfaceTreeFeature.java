package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.level.generator.feature.ObjectGeneratorFeature;
import org.powernukkitx.level.generator.object.ObjectFallenTree;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.ObjectSmallSpruceTree;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class IceSurfaceTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:ice_surface_trees_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return random.nextInt(100) == 0 ? new ObjectFallenTree(WoodType.SPRUCE) : new ObjectSmallSpruceTree();
    }

    @Override
    public int getMin() {
        return -20;
    }

    @Override
    public int getMax() {
        return 1;
    }
}
