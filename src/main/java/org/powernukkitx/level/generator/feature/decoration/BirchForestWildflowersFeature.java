package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.level.generator.feature.SurfaceCandidateFeature;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementPredicate;
import org.powernukkitx.level.generator.feature.placement.vegetation.WildflowersPlacementFeature;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class BirchForestWildflowersFeature extends SurfaceCandidateFeature {

    public static final String NAME = "minecraft:scatter_birch_forest_wildflowers_feature";

    private static final PlacementFeature WILDFLOWERS = WildflowersPlacementFeature.scatter();

    @Override
    protected int iterations() {
        return 3;
    }

    @Override
    protected boolean shouldApply(RandomSourceProvider random) {
        return random.nextExclusiveInt(2) == 0;
    }

    @Override
    protected PlacementFeature placementFeature() {
        return WILDFLOWERS;
    }

    @Override
    protected PlacementPredicate candidatePredicate() {
        return BirchForestWildflowersFeature::isBirchForestCandidate;
    }

    private static boolean isBirchForestCandidate(FeaturePlacementContext context) {
        int biomeId = context.getGenerationContext().getLevel().getBiomeId(
                context.getX(),
                context.getY(),
                context.getZ()
        );

        return Registries.BIOME.containsTag(BiomeTags.BIRCH, biomeId)
                && Registries.BIOME.containsTag(BiomeTags.FOREST, biomeId)
                && !Registries.BIOME.containsTag(BiomeTags.HILLS, biomeId);
    }

    @Override
    public String name() {
        return NAME;
    }
}
