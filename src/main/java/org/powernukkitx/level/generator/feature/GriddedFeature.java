package org.powernukkitx.level.generator.feature;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;

public abstract class GriddedFeature extends ObjectGeneratorFeature {

    public int getSplit() {
        return 2;
    }

    public int getDistanceToNextField() {
        return getSplit() > splitLength() ? splitLength() / 2 : getSplit();
    }

    protected int splitLength() {
        return 16/getSplit();
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        BlockManager object = new BlockManager(level);
        for (int x = 0; x < getSplit(); x++) {
            for (int z = 0; z < getSplit(); z++) {
                this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ)^ (x + z) ^ name().hashCode());

                int placeX = getDistanceToNextField() + random.nextInt(splitLength() - getDistanceToNextField()) + (x * splitLength()) + (chunkX << 4);
                int placeZ = getDistanceToNextField() + random.nextInt(splitLength() - getDistanceToNextField()) + (z * splitLength()) + (chunkZ << 4);
                int placeY = level.getHeightMap(placeX, placeZ);

                if(!canSpawnHere(Registries.BIOME.get(level.getBiomeId(placeX, placeY, placeZ)).second())) continue;
                if(isSupportDirt(level.getBlock(placeX, placeY, placeZ))) {
                    getGenerator(random).generate(object, random, new Vector3(placeX, placeY + 1, placeZ));
                }
            }
        }
        queueObject(chunk, object);
    }

    @Override
    public final int getMax() {
        return super.getMax();
    }

    @Override
    public final int getMin() {
        return super.getMin();
    }
}
