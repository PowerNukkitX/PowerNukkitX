package cn.nukkit.level.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;

public abstract class GenerateFeature extends Populator {

    public BlockManager getChunkPlacementQueue(Long chunkHash, Level level) {
        if(!PLACEMENT_QUEUE.containsKey(chunkHash)) PLACEMENT_QUEUE.put(chunkHash, new BlockManager(level));
        return PLACEMENT_QUEUE.get(chunkHash);
    }

    public abstract String name();

    public String identifier() {
        return name();
    }

    public abstract void apply(ChunkGenerateContext context);

}
