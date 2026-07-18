package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;

import java.util.concurrent.Executor;

public class FinishedStage extends GenerateStage {
    public static final String NAME = "finished";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        chunk.setChunkState(ChunkState.FINISHED);
        if(!chunk.getLevel().getServer().getSettings().chunkSettings().saveGenerated()) {
            chunk.setChanged(false);
        }
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
