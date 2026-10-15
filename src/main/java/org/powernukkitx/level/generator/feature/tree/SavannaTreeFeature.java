package org.powernukkitx.level.generator.feature.tree;

public class SavannaTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:savanna_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> random.nextExclusiveInt(5) > 0
            ? TreePlacementFeatures.savanna()
            : TreePlacementFeatures.oak();

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            1.0f,
            TreePlacementFeatures::isSavannaCandidate,
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
