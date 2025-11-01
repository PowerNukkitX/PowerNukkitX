package cn.nukkit.level.generator.feature.multi;

import cn.nukkit.level.generator.feature.MultiFeatureWrapper;
import cn.nukkit.level.generator.feature.decoration.HugeMushroomFeature;
import cn.nukkit.level.generator.feature.decoration.ReedsFeature;
import cn.nukkit.level.generator.feature.tree.SwampTreeFeature;

public class SwampFoliageFeature extends MultiFeatureWrapper {

    public static final String NAME = "minecraft:legacy:swamp_foliage_feature";

    @Override
    protected String[] getFeatures() {
        return new String[] {
                ReedsFeature.NAME,
                HugeMushroomFeature.NAME,
                SwampTreeFeature.NAME
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
