package cn.nukkit.level.generator.feature.tree;

import cn.nukkit.tags.BiomeTags;

public class MeadowTreeFeature extends PlainsTreeFeature {

    public static final String NAME = "minecraft:meadow_surface_trees_feature";

    @Override
    public String getRequiredTag() {
        return BiomeTags.MEADOW;
    }

    @Override
    public String name() {
        return NAME;
    }
}
