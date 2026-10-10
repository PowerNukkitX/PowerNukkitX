package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;
import org.powernukkitx.level.generator.object.BlockManager;

import java.util.concurrent.Executor;

public class GeneratedStage extends GenerateStage {
    public static final String NAME = "generated";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        chunk.setFinalizationState(ChunkFinalizationState.NEEDS_POPULATION);
        chunk.setChanged(false);
        BlockManager.applyPendingSubChunkUpdates(context.getLevel(), chunk);
    }

    @Override
    public Executor getExecutor() {
        return Runnable::run;
    }

    @Override
    public String name() {
        return NAME;
    }
}
