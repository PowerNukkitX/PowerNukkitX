package org.powernukkitx.level.generator.feature.tree;

public class JungleTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:jungle_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(10) == 0) {
            return TreePlacementFeatures.fancyOak();
        }
        if (random.nextExclusiveInt(2) == 0) {
            return TreePlacementFeatures.jungleBush();
        }
        if (random.nextExclusiveInt(3) == 0) {
            return TreePlacementFeatures.megaJungle();
        }
        return TreePlacementFeatures.jungle();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            50.0f,
            TreePlacementFeatures::isJungleCandidate,
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
