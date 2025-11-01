package cn.nukkit.level.generator.feature;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class CountGenerateFeature extends GenerateFeature {

    public abstract int getBase();
    public abstract int getRandom();

    public abstract void populate(ChunkGenerateContext context, RandomSourceProvider random);

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());
        int count = getBase() + random.nextBoundedInt(getRandom());
        for (int i = 0; i < count; i++) {
            populate(context, random);
        }
    }
}
