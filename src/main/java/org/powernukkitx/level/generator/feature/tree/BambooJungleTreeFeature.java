package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.placement.vegetation.TallGrassAroundTreePlacementFeature;

public class BambooJungleTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:bamboo_jungle_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(20) == 0) {
            return TreePlacementFeatures.fancyOak();
        }
        if (random.nextExclusiveInt(20) <= 2) {
            return TreePlacementFeatures.jungleBush();
        }
        if (random.nextExclusiveInt(10) <= 6) {
            return TreePlacementFeatures.megaJungle();
        }
        return TallGrassAroundTreePlacementFeature.scatter();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            30.0f,
            TreePlacementFeatures::isBambooJungleCandidate,
            SELECTOR
    );

    @Override
    protected TreeFeaturePlan treePlan() {
        return PLAN;
    }

    @Override
    public String name() {
        return NAME;
    }
}
