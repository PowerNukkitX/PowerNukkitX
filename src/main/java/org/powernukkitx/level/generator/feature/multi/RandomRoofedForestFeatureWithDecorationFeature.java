package org.powernukkitx.level.generator.feature.multi;

import org.powernukkitx.level.generator.feature.MultiFeatureWrapper;
import org.powernukkitx.level.generator.feature.decoration.ForestFoliageFeature;
import org.powernukkitx.level.generator.feature.decoration.HugeMushroomFeature;
import org.powernukkitx.level.generator.feature.tree.RoofedForestTreeFeature;

public class RandomRoofedForestFeatureWithDecorationFeature extends MultiFeatureWrapper {

    public static final String NAME = "minecraft:random_roofed_forest_feature_with_decoration_feature";

    @Override
    protected String[] getFeatures() {
        return new String[] {
                ForestFoliageFeature.NAME,
                HugeMushroomFeature.NAME,
                RoofedForestTreeFeature.NAME
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
