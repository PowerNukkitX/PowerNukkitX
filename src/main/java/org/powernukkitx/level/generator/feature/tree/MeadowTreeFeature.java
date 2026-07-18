package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.tags.BiomeTags;

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
