package cn.nukkit.level.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.random.NukkitRandom;

public abstract class GenerateFeature {

    public abstract String name();

    public String identifier() {
        return name();
    }

    public abstract void apply(ChunkGenerateContext context);

    protected NukkitRandom getChunkLocalRandom(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        return new NukkitRandom(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
    }

}
