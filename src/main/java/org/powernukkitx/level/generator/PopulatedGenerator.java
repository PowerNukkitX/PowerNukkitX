package org.powernukkitx.level.generator;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.stages.GeneratedStage;

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
    protected String getEndName(IChunk chunk) {
        return chunk.getChunkState().ordinal() >= ChunkState.GENERATED.ordinal() ||
                level.getChunkLoaders(chunk.getX(), chunk.getZ()).length != 0 ? end.name() : getLastTerrainStage();
    }

    @Override
    protected GenerateStage getStart(ChunkGenerateContext context) {
        ChunkState state = context.getChunk().getChunkState();
        if(state.ordinal() >= ChunkState.GENERATED.ordinal()) {
            return getStartPopulateStage();
        }
        return super.getStart(context);
    }

    protected GenerateStage getStartPopulateStage() {
        if(startPopulateStage == null) {
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
