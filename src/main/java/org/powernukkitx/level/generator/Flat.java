package org.powernukkitx.level.generator;

import org.powernukkitx.Server;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.level.generator.stages.flat.FlatGenerateStage;
import org.powernukkitx.level.generator.stages.LightPopulationStage;
import org.powernukkitx.registry.Registries;

import java.util.List;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class Flat extends Generator {
    public Flat(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(FlatGenerateStage.NAME));
        if (Server.getInstance().getSettings().chunkSettings().lightUpdates()) {
            builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        }
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public List<ChunkGenerationTask> getGenerationTasks() {
        final String finalStage = Server.getInstance().getSettings().chunkSettings().lightUpdates()
                ? LightPopulationStage.NAME : FinishedStage.NAME;

        return List.of(
                new ChunkGenerationTask(
                        "Chunk Gen",
                        ChunkGenerationState.NEEDS_GENERATION,
                        ChunkGenerationState.GENERATING,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        ChunkGenerationState.NEEDS_GENERATION,
                        FlatGenerateStage.NAME,
                        FlatGenerateStage.NAME,
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
                        "Decoration Post Processing",
                        ChunkGenerationState.NEEDS_POPULATION,
                        ChunkGenerationState.POPULATING,
                        ChunkGenerationState.NEEDS_CFRD,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        null,
                        null,
                        ChunkGenerationDependency.NEIGHBORHOOD_PRESENT
                ),

                new ChunkGenerationTask(
                        "Chunk CFRD",
                        ChunkGenerationState.NEEDS_CFRD,
                        ChunkGenerationState.CFRD,
                        ChunkGenerationState.NEEDS_LIGHTING,
                        ChunkGenerationState.NEEDS_CFRD,
                        finalStage,
                        FinishedStage.NAME,
                        ChunkGenerationDependency.NEIGHBORHOOD_PRESENT
                )
        );
    }

    @Override
    public String getName() {
        return "flat";
    }
}
