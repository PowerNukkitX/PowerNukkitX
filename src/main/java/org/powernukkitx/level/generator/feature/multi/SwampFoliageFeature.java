package org.powernukkitx.level.generator.feature.multi;

import org.powernukkitx.level.generator.feature.MultiFeatureWrapper;
import org.powernukkitx.level.generator.feature.decoration.HugeMushroomFeature;
import org.powernukkitx.level.generator.feature.decoration.ReedsFeature;
import org.powernukkitx.level.generator.feature.tree.SwampTreeFeature;

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
