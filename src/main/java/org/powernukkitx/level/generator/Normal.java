package org.powernukkitx.level.generator;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.level.generator.biome.result.OverworldBiomeResult;
import org.powernukkitx.level.generator.holder.ObjectHolder;
import org.powernukkitx.level.generator.holder.NormalObjectHolder;
import org.powernukkitx.level.generator.stages.GeneratedStage;
import org.powernukkitx.level.generator.stages.LightPopulationStage;
import org.powernukkitx.level.generator.stages.NormalChunkFeatureStage;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.level.generator.stages.BiomeMapStage;
import org.powernukkitx.level.generator.stages.normal.NormalPopulatorStage;
import org.powernukkitx.level.generator.stages.normal.NormalSurfaceDataStage;
import org.powernukkitx.level.generator.stages.normal.NormalSurfaceOverwriteStage;
import org.powernukkitx.level.generator.stages.normal.NormalTerrainStage;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.Map;

/**
 * @author Buddelbubi
 */
public class Normal extends PopulatedGenerator implements BiomedGenerator {

    public Normal(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(NormalTerrainStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(BiomeMapStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalSurfaceDataStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalSurfaceOverwriteStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(GeneratedStage.NAME));

        builder.next(Registries.GENERATE_STAGE.get(NormalPopulatorStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalChunkFeatureStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public BiomePicker<OverworldBiomeResult> createBiomePicker(Level level) {
        return new OverworldBiomePicker(level);
    }

    @Override
    public ObjectHolder createObjectHolder(Level level) {
        return new NormalObjectHolder(new Xoroshiro128(level.getSeed()));
    }

    @Override
    public String getName() {
        return "normal";
    }

}
