package cn.nukkit.level.generator.feature.foliage;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.NukkitRandom;

public abstract class SurfaceGenerateFeature extends CountGenerateFeature {

    @Override
    public void populate(ChunkGenerateContext context, NukkitRandom random) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int x = random.nextBoundedInt(15);
        int z = random.nextBoundedInt(15);
        int y = context.getChunk().getHeightMap(x, z);
        if (y > 0 && isSupportValid(chunk.getBlockState(x, y, z).toBlock())) {
            BlockManager manager = new BlockManager(chunk.getLevel());
            BlockManager object = new BlockManager(chunk.getLevel());
            place(object, (chunkX << 4) + x, y+1, (chunkZ << 4) + z);
            for(Block block : object.getBlocks()) {
                if(block.getChunk() != chunk) {
                    IChunk nextChunk = block.getChunk();
                    long chunkHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                    getChunkPlacementQueue(chunkHash, chunk.getLevel()).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                }
                if(block.getChunk().isGenerated()) {
                    manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                }
            }
            writeOutsideChunkStructureData(chunk);
            manager.applySubChunkUpdate();
        }
    }

    public abstract void place(BlockManager manager, int x, int y, int z);

    public boolean isSupportValid(Block support) {
        return support instanceof BlockGrassBlock;
    }

}
