package org.powernukkitx.level.generator.feature.tree;

public class MegaTaigaTreeFeature extends TreeRuleFeature {
    public static final String NAME = "minecraft:mega_taiga_surface_trees_feature";

    private static final TreeFeatureSelector SELECTOR = random -> {
        if (random.nextExclusiveInt(3) == 0) {
            return random.nextExclusiveInt(13) == 0
                    ? TreePlacementFeatures.megaSpruce()
                    : TreePlacementFeatures.megaPine();
        }

        return random.nextExclusiveInt(3) == 0
                ? TreePlacementFeatures.pine()
                : TreePlacementFeatures.spruce();
    };

    private static final TreeFeaturePlan PLAN = TreeFeaturePlan.legacy(
            10.0f,
            TreePlacementFeatures::isMegaTaigaCandidate,
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
