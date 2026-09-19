package org.powernukkitx.level.generator.stages;

import org.cloudburstmc.protocol.bedrock.data.biome.BiomeConsolidatedFeatureData;
import org.powernukkitx.registry.Registries;

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
    protected boolean shouldApplyFeature(BiomeConsolidatedFeatureData feature) {
        return PREGENERATION_PASS.equals(Registries.BIOME.getFromBiomeStringList(feature.getPass()));
    }

    @Override
    public String name() {
        return NAME;
    }
}
