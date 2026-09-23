package org.powernukkitx.level.generator;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.stages.GeneratedStage;

import java.util.List;
import java.util.Map;

public abstract class PopulatedGenerator extends Generator {
    private GenerateStage startPopulateStage;

    public PopulatedGenerator(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    public String getLastTerrainStage() {
        return GeneratedStage.NAME;
    }

    @Override
    public List<ChunkGenerationTask> getGenerationTasks() {
        GenerateStage terrainEnd = getStartPopulateStage();
        GenerateStage populationStart = terrainEnd == null ? null : terrainEnd.getNextStage();

        if (populationStart == null) return super.getGenerationTasks();

        return List.of(
                new ChunkGenerationTask(
                        "Chunk Gen",
                        ChunkGenerationState.NEEDS_GENERATION,
                        ChunkGenerationState.GENERATING,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        ChunkGenerationState.NEEDS_GENERATION,
                        start.name(),
                        getLastTerrainStage(),
                        ChunkGenerationDependency.NONE
                ),

                new ChunkGenerationTask(
                        "Chunk Post Processing",
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        ChunkGenerationState.STRUCTURE_PP,
                        ChunkGenerationState.NEEDS_LIGHTING,
                        ChunkGenerationState.NEEDS_STRUCTURE_PP,
                        populationStart.name(),
                        end.name(),
                        ChunkGenerationDependency.NEIGHBORHOOD_GENERATED
                )
        );
    }

    @Override
    protected String getEndName(IChunk chunk) {
        return chunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING ||
                level.getChunkLoaders(chunk.getX(), chunk.getZ()).length != 0 ? end.name() : getLastTerrainStage();
    }

    @Override
    protected GenerateStage getStart(ChunkGenerateContext context) {
        ChunkFinalizationState state = context.getChunk().getFinalizationState();
        if (state != ChunkFinalizationState.NEEDS_INSTATICKING) {
            return getStartPopulateStage();
        }
        return super.getStart(context);
    }

    protected GenerateStage getStartPopulateStage() {
        if (startPopulateStage == null) {
            GenerateStage stage = start;
            while((stage = stage.getNextStage()) != null && !stage.name().equals(getLastTerrainStage()));
            startPopulateStage = stage;
        }
        return startPopulateStage;
    }

    protected void asyncPopulate(IChunk chunk) {
        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);
        asyncGenerate0(context, getStartPopulateStage(), end.name(), () -> {});
    }
}
