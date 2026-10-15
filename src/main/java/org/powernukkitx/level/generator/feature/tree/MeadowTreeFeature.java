package org.powernukkitx.level.generator.feature.tree;

public class MeadowTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:meadow_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> random.nextBoolean()
            ? TreePlacementFeatures.fancyOakWithBeehive()
            : TreePlacementFeatures.superBirchWithBeehive();

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            0.01f,
            TreePlacementFeatures::isMeadowCandidate,
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
