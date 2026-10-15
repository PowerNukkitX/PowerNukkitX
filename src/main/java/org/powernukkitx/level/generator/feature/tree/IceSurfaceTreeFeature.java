package org.powernukkitx.level.generator.feature.tree;

public class IceSurfaceTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:ice_surface_trees_feature";

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            0.1f,
            TreePlacementFeatures::isIceCandidate,
            TreeFeatureSelector.fixed(TreePlacementFeatures.spruce())
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
