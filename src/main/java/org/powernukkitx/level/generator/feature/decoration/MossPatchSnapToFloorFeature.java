package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.level.generator.feature.VolumeCandidateFeature;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementPredicate;
import org.powernukkitx.level.generator.feature.placement.SnapToFloorPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.vegetation.MossPatchPlacementFeature;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;

/**
 * Native lush-cave moss-patch feature rule.
 */
public class MossPatchSnapToFloorFeature extends VolumeCandidateFeature {

    public static final String NAME = "minecraft:moss_patch_snap_to_floor_feature";

    private static final PlacementFeature MOSS_PATCH = new SnapToFloorPlacementFeature(
            12,
            MossPatchPlacementFeature.feature()
    );

    @Override
    protected int iterations() {
        return 125;
    }

    @Override
    protected int minY() {
        return -64;
    }

    @Override
    protected int maxYExclusive() {
        return 256;
    }

    @Override
    protected PlacementFeature placementFeature() {
        return MOSS_PATCH;
    }

    @Override
    protected PlacementPredicate candidatePredicate() {
        return MossPatchSnapToFloorFeature::isLushCaveCandidate;
    }

    private static boolean isLushCaveCandidate(FeaturePlacementContext context) {
        int biomeId = context.getGenerationContext().getLevel().getBiomeId(
                context.getX(),
                context.getY(),
                context.getZ()
        );
        return Registries.BIOME.containsTag(BiomeTags.LUSH_CAVES, biomeId);
    }

    @Override
    public String name() {
        return NAME;
    }
}
