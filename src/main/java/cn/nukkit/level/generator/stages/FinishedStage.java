package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

import java.util.concurrent.Executor;

public class FinishedStage extends GenerateStage {
    public static final String $1 = "finished";

    @Override
    /**
     * @deprecated 
     */
    
    public void apply(ChunkGenerateContext context) {
        IChunk $2 = context.getChunk();
        chunk.setChunkState(ChunkState.FINISHED);
    }

    @Override
    public Executor getExecutor() {
        return Runnable::run;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String name() {
        return NAME;
    }
}
