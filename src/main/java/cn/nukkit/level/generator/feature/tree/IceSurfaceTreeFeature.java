package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.ObjectSmallSpruceTree;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class IceSurfaceTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:ice_surface_trees_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public ObjectGenerator getGenerator(RandomSourceProvider random) {
        return new ObjectSmallSpruceTree();
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
