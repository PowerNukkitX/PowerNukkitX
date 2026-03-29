package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

import java.util.concurrent.Executor;

public class GeneratedStage extends GenerateStage {
    public static final String NAME = "generated";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        chunk.setChunkState(ChunkState.GENERATED);
        //chunk.getProvider().saveChunk(chunk.getX(), chunk.getZ(), chunk);
        chunk.setChanged(false);
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
