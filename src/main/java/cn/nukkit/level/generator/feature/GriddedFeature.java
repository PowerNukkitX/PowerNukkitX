package cn.nukkit.level.generator.feature;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;

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
                this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ)^ (x + z));

                int placeX = getDistanceToNextField() + random.nextInt(splitLength() - getDistanceToNextField()) + (x * splitLength()) + (chunkX << 4);
                int placeZ = getDistanceToNextField() + random.nextInt(splitLength() - getDistanceToNextField()) + (z * splitLength()) + (chunkZ << 4);
                int placeY = level.getHeightMap(placeX, placeZ);

                if(!canSpawnHere(Registries.BIOME.get(level.getBiomeId(placeX, placeY, placeZ)))) continue;
                if(isSupportValid(level.getBlock(placeX, placeY, placeZ))) {
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
