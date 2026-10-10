package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.generator.feature.FeaturePlacementPass;

import java.util.List;

/**
 * Runs pregeneration-pass biome features before structure post-processing.
 *
 * @author Curse
 */
public class NormalPregenerationFeatureStage extends NormalChunkFeatureStage {

    public static final String NAME = "pregeneration_feature";

    @Override
    protected boolean cacheBiomesForPopulation() {
        return true;
    }

    @Override
    protected List<FeaturePlacementPass> placementPasses() {
        return List.of(FeaturePlacementPass.PREGENERATION);
    }

    @Override
    public String name() {
        return NAME;
    }
}
