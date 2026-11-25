package org.powernukkitx.level.generator.feature.tree;

/**
 * Populates the dappled forest with poplar and spruce trees.
 *
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/11/01
 */
public class DappledForestTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:select_tree_dappled_forest_feature";

    private static final TreeFeatureSelector SELECTOR = random -> random.nextExclusiveInt(20) == 0
            ? TreePlacementFeatures.spruce()
            : TreePlacementFeatures.poplarWithFallenTree();

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            7.0f,
            TreePlacementFeatures::isDappledForestCandidate,
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
