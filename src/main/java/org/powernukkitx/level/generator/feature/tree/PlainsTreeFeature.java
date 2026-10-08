package org.powernukkitx.level.generator.feature.tree;

public class PlainsTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:plains_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> random.nextExclusiveInt(3) == 0
            ? TreePlacementFeatures.fancyOakWithOptionalBeehive()
            : TreePlacementFeatures.oak();

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            0.05f,
            TreePlacementFeatures::isPlainsCandidate,
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
