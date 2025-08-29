package cn.nukkit.level.generator.feature.surface;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
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
            BlockManager object = new BlockManager(chunk.getLevel());
            place(object, (chunkX << 4) + x, y+1, (chunkZ << 4) + z);
            if(object.getBlocks().stream().noneMatch(block -> !block.getChunk().isGenerated())) {
                object.applySubChunkUpdate(object.getBlocks());
            }
        }
    }

    public abstract void place(BlockManager manager, int x, int y, int z);

    public boolean isSupportValid(Block support) {
        return support instanceof BlockGrassBlock;
    }

}
