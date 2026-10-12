package org.powernukkitx.level.generator.feature.tree;

public class TaigaTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:taiga_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> random.nextExclusiveInt(3) == 0
            ? TreePlacementFeatures.pine()
            : TreePlacementFeatures.spruce();

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            10.0f,
            TreePlacementFeatures::isTaigaCandidate,
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
