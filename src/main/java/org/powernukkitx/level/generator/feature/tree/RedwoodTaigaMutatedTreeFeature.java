package org.powernukkitx.level.generator.feature.tree;

/**
 * Mutated redwood-taiga tree rule.
 *
 * @author Curse
 */
public class RedwoodTaigaMutatedTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:redwood_taiga_mutated_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(3) == 0) {
            return TreePlacementFeatures.megaSpruce();
        }

        return random.nextExclusiveInt(3) == 0
                ? TreePlacementFeatures.pine()
                : TreePlacementFeatures.spruce();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            10.0f,
            TreePlacementFeatures::isMutatedRedwoodTaigaCandidate,
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
