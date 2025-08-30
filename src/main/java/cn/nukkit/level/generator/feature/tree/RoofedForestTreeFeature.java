package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.level.generator.feature.ObjectGeneratorFeature;
import cn.nukkit.level.generator.object.ObjectBigMushroom;
import cn.nukkit.level.generator.object.ObjectDarkOakTree;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public class RoofedForestTreeFeature extends ObjectGeneratorFeature {

    public static final String NAME = "minecraft:roofed_forest_surface_roofed_tree_feature_rules";

    @Override
    public ObjectGenerator getGenerator(NukkitRandom random) {
        return new ObjectDarkOakTree();
    }

    @Override
    public int getMin() {
        return 8;
    }

    @Override
    public int getMax() {
        return 10;
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.ROOFED;
    }

    @Override
    public String name() {
        return NAME;
    }
}
