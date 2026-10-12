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
import org.powernukkitx.level.generator.stages.NormalPregenerationFeatureStage;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.level.generator.stages.BiomeMapStage;
import org.powernukkitx.level.generator.stages.normal.NormalPopulatorStage;
import org.powernukkitx.level.generator.stages.normal.NormalSurfaceDataStage;
import org.powernukkitx.level.generator.stages.normal.NormalSurfaceOverwriteStage;
import org.powernukkitx.level.generator.stages.normal.NormalTerrainStage;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.List;
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
        builder.next(Registries.GENERATE_STAGE.get(NormalPregenerationFeatureStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(GeneratedStage.NAME));

        builder.next(Registries.GENERATE_STAGE.get(NormalPopulatorStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalChunkFeatureStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public List<ChunkGenerationTask> getGenerationTasks() {
        return List.of(
                new ChunkGenerationTask(
                        "Chunk Gen",
                        ChunkGenerationState.NEEDS_GENERATION,
                        ChunkGenerationState.GENERATING,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        ChunkGenerationState.NEEDS_GENERATION,
                        NormalTerrainStage.NAME,
                        GeneratedStage.NAME,
                        ChunkGenerationDependency.NONE
                ),

                new ChunkGenerationTask(
                        "Chunk Structure PP",
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        ChunkGenerationState.STRUCTURE_PP,
                        ChunkGenerationState.NEEDS_POPULATION,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        NormalPopulatorStage.NAME,
                        NormalPopulatorStage.NAME,
                        ChunkGenerationDependency.NEIGHBORHOOD_GENERATED
                ),

                new ChunkGenerationTask(
                        "Chunk PP",
                        ChunkGenerationState.NEEDS_POPULATION,
                        ChunkGenerationState.POPULATING,
                        ChunkGenerationState.NEEDS_CFRD,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        NormalChunkFeatureStage.NAME,
                        NormalChunkFeatureStage.NAME,
                        ChunkGenerationDependency.NEIGHBORHOOD_PRESENT
                ),

                new ChunkGenerationTask(
                        "Chunk CFRD",
                        ChunkGenerationState.NEEDS_CFRD,
                        ChunkGenerationState.CFRD,
                        ChunkGenerationState.NEEDS_LIGHTING,
                        ChunkGenerationState.NEEDS_CFRD,
                        LightPopulationStage.NAME,
                        FinishedStage.NAME,
                        ChunkGenerationDependency.NEIGHBORHOOD_PRESENT
                )
        );
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
