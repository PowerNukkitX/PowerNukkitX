package cn.nukkit.level.generator.feature;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.utils.random.NukkitRandom;

public abstract class CountGeneratorFeature extends GenerateFeature {

    public abstract int getBase();
    public abstract int getRandom();

    public abstract void populate(ChunkGenerateContext context, NukkitRandom random);

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        NukkitRandom random = new NukkitRandom(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int count = getBase() + random.nextBoundedInt(getRandom());
        for (int i = 0; i < count; i++) {
            populate(context, random);
        }
    }
}
