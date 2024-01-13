package cn.nukkit.level.generator;

import cn.nukkit.Server;

import java.util.concurrent.Executor;

public interface GenerateStage {
    void apply(ChunkGenerateContext context);

    default Executor getExecutor() {
        return Server.getInstance().computeThreadPool;
    }
}
