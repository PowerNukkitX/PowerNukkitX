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
        return splitLength() / getSplit();
    }

    protected int splitLength() {
        return 16/getSplit();
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        long chunkHash = Level.chunkHash(chunkX, chunkZ);
        Level level = chunk.getLevel();
        BlockManager manager = new BlockManager(level);
        BlockManager object = new BlockManager(level);
        for (int x = 0; x < getSplit(); x++) {
            for (int z = 0; z < getSplit(); z++) {
                NukkitRandom random = new NukkitRandom(level.getSeed() ^ chunkHash ^ (x + z));
                int placeX = getDistanceToNextField() + random.fork().nextInt(splitLength() - getDistanceToNextField()) + (x * splitLength()) + (chunkX << 4);
                int placeZ = getDistanceToNextField() + random.fork().nextInt(splitLength() - getDistanceToNextField()) + (z * splitLength()) + (chunkZ << 4);
                int placeY = level.getHeightMap(placeX, placeZ);

                if(!canSpawnHere(Registries.BIOME.get(level.getBiomeId(placeX, placeY, placeZ)))) continue;
                if(isSupportValid(level.getBlock(placeX, placeY, placeZ))) {
                    getGenerator(random.fork()).generate(object, random.fork(), new Vector3(placeX, placeY + 1, placeZ));
                }
            }
        }
        for(Block block : object.getBlocks()) {
            if(block.getChunk() != chunk) {
                IChunk nextChunk = block.getChunk();
                long targetHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                getChunkPlacementQueue(targetHash, level).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
            }
            if(block.getChunk().isGenerated()) {
                manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
            }
        }
        writeOutsideChunkStructureData(chunk);
        manager.applySubChunkUpdate();
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
