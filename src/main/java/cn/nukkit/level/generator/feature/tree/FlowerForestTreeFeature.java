package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.tags.BiomeTags;

public class FlowerForestTreeFeature extends ForestTreeFeature {

    public static final String NAME = "minecraft:flower_forest_surface_trees_feature";

    @Override
    public int getMin() {
        return 8;
    }

    @Override
    public int getMax() {
        return 6;
    }

    @Override
    public String getRequiredTag() {
        return BiomeTags.FLOWER_FOREST;
    }

    @Override
    public String name() {
        return NAME;
    }
}
