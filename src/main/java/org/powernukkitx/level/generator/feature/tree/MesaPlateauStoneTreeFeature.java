package org.powernukkitx.level.generator.feature.tree;

public class MesaPlateauStoneTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:mesa_plateau_stone_surface_trees_feature";

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            5.0f,
            TreePlacementFeatures::isMesaStoneCandidate,
            TreeFeatureSelector.fixed(TreePlacementFeatures.oakWithLeafLitter())
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
