package org.powernukkitx.level.generator.feature.tree;

public class FlowerForestTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:flower_forest_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(5) == 0) {
            return TreePlacementFeatures.birch();
        }

        return random.nextExclusiveInt(10) == 0
                ? TreePlacementFeatures.fancyOakWithOptionalBeehive()
                : TreePlacementFeatures.oakWithOptionalBeehive();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            6.0f,
            TreePlacementFeatures::isFlowerForestCandidate,
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
