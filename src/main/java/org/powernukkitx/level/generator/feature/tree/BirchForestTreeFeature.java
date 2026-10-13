package org.powernukkitx.level.generator.feature.tree;

public class BirchForestTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:birch_forest_surface_trees_feature";

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            10.0f,
            TreePlacementFeatures::isBirchForestCandidate,
            TreeFeatureSelector.fixed(TreePlacementFeatures.birch())
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
