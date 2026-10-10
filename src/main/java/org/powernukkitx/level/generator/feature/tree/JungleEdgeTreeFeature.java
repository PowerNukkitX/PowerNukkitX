package org.powernukkitx.level.generator.feature.tree;

public class JungleEdgeTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:jungle_edge_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(10) == 0) {
            return TreePlacementFeatures.fancyOakWithOptionalBeehive();
        }
        if (random.nextExclusiveInt(2) == 0) {
            return TreePlacementFeatures.jungleBush();
        }
        return TreePlacementFeatures.jungle();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            2.0f,
            TreePlacementFeatures::isJungleEdgeCandidate,
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
