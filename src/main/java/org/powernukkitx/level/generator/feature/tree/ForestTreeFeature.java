package org.powernukkitx.level.generator.feature.tree;

public class ForestTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:forest_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(5) == 0) {
            return TreePlacementFeatures.birchWithLeafLitter();
        }
        if (random.nextExclusiveInt(10) == 0) {
            return TreePlacementFeatures.fancyOakWithLeafLitterAndOptionalBeehive();
        }
        return TreePlacementFeatures.oakWithLeafLitter();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            10.0f,
            TreePlacementFeatures::isNormalForestCandidate,
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
