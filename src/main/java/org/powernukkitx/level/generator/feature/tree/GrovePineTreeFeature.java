package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.tags.BiomeTags;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/11/01
 */
public class GrovePineTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:grove_pine_tree_feature";

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            4.0f,
            context -> TreePlacementFeatures.hasTag(context, BiomeTags.GROVE),
            random -> TreePlacementFeatures.pine()
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
