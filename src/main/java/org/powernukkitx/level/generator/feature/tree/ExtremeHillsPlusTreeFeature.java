package org.powernukkitx.level.generator.feature.tree;

/**
 * Legacy extreme-hills-plus tree rule.
 *
 * @author Curse
 */
public class ExtremeHillsPlusTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:extreme_hills_plus_trees_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(3) != 0) {
            return TreePlacementFeatures.spruce();
        }

        return random.nextExclusiveInt(10) == 0
                ? TreePlacementFeatures.fancyOak()
                : TreePlacementFeatures.oak();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            3.0f,
            TreePlacementFeatures::isExtremeHillsCandidate,
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
