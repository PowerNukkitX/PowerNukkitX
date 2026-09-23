package org.powernukkitx.level.generator;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.NetherBiomePicker;
import org.powernukkitx.level.generator.biome.result.NetherBiomeResult;
import org.powernukkitx.level.generator.holder.ObjectHolder;
import org.powernukkitx.level.generator.holder.NetherObjectHolder;
import org.powernukkitx.level.generator.stages.BiomeMapStage;
import org.powernukkitx.level.generator.stages.GeneratedStage;
import org.powernukkitx.level.generator.stages.LightPopulationStage;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.level.generator.stages.nether.NetherPopulatorStage;
import org.powernukkitx.level.generator.stages.nether.NetherTerrainStage;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.NukkitRandom;

import java.util.List;
import java.util.Map;

public class Nether extends PopulatedGenerator implements BiomedGenerator {

    public Nether(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(BiomeMapStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NetherTerrainStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(GeneratedStage.NAME));

        builder.next(Registries.GENERATE_STAGE.get(NetherPopulatorStage.NAME));
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
                        BiomeMapStage.NAME,
                        GeneratedStage.NAME,
                        ChunkGenerationDependency.NONE
                ),

                new ChunkGenerationTask(
                        "Structure Post Processing",
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        ChunkGenerationState.STRUCTURE_PP,
                        ChunkGenerationState.NEEDS_POPULATION,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        null,
                        null,
                        ChunkGenerationDependency.NEIGHBORHOOD_GENERATED
                ),

                new ChunkGenerationTask(
                        "Chunk PP",
                        ChunkGenerationState.NEEDS_POPULATION,
                        ChunkGenerationState.POPULATING,
                        ChunkGenerationState.NEEDS_CFRD,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        NetherPopulatorStage.NAME,
                        NetherPopulatorStage.NAME,
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
    public BiomePicker<NetherBiomeResult> createBiomePicker(Level level) {
        return new NetherBiomePicker(new NukkitRandom(level.getSeed()));
    }

    @Override
    public ObjectHolder createObjectHolder(Level level) {
        return new NetherObjectHolder(new NukkitRandom(level.getSeed()));
    }

    @Override
    public String getName() {
        return "nether";
    }
}
