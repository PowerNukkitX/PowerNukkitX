package org.powernukkitx.level.generator.feature.multi;

import org.powernukkitx.level.generator.feature.MultiFeatureWrapper;
import org.powernukkitx.level.generator.feature.decoration.JungleGrassFeature;
import org.powernukkitx.level.generator.feature.decoration.TallGrassGenerateFeature;
import org.powernukkitx.level.generator.feature.tree.JungleBushFeature;

public class JunglePlantFeature extends MultiFeatureWrapper {

    public static final String NAME = "minecraft:scatter_jungle_plant_feature";

    @Override
    protected String[] getFeatures() {
        return new String[] {
                JungleBushFeature.NAME,
                JungleGrassFeature.NAME,
                TallGrassGenerateFeature.NAME
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
