package org.powernukkitx.level.generator.feature.tree;

public class BirchForestMutatedTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:birch_forest_mutated_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(5) == 0) {
            return TreePlacementFeatures.birch();
        }

        return random.nextBoolean()
                ? TreePlacementFeatures.superBirch()
                : TreePlacementFeatures.birch();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            10.0f,
            TreePlacementFeatures::isMutatedBirchForestCandidate,
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
