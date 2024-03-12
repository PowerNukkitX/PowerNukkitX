package cn.nukkit.level.generator.stages;

import cn.nukkit.Server;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.plugin.InternalPlugin;

import java.util.concurrent.Executor;

public class FinishedStage extends GenerateStage {
    public static final String NAME = "finished";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        chunk.setChunkState(ChunkState.FINISHED);
    }

    @Override
    public Executor getExecutor() {
        return (r) -> Server.getInstance().getScheduler().scheduleTask(InternalPlugin.INSTANCE, r);//run in main thread
    }

    @Override
    public String name() {
        return NAME;
    }
}
