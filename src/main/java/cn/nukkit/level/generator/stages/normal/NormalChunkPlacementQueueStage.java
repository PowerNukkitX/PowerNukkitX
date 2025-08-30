package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.Normal;

public class NormalChunkPlacementQueueStage extends GenerateStage {

    public static final String NAME = "normal_chunkplacementqueue";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if(context.getGenerator() instanceof Normal generator) {
            long chunkHash = Level.chunkHash(chunk.getX(), chunk.getZ());
            generator.getChunkPlacementQueue(chunkHash).applySubChunkUpdate();
            generator.removeFromPlacementQueue(chunkHash);
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
