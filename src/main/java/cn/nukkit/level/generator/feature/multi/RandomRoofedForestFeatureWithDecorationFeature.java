package cn.nukkit.level.generator.feature.multi;

import cn.nukkit.level.generator.feature.MultiFeatureWrapper;
import cn.nukkit.level.generator.feature.decoration.ForestFoliageFeature;
import cn.nukkit.level.generator.feature.decoration.HugeMushroomFeature;
import cn.nukkit.level.generator.feature.tree.RoofedForestTreeFeature;

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
