package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.object.BlockManager;

import java.util.concurrent.Executor;

public class GeneratedStage extends GenerateStage {
    public static final String NAME = "generated";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        chunk.setChunkState(ChunkState.GENERATED);
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
