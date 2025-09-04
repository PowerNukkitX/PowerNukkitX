package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public abstract class SurfaceGenerateFeature extends CountGenerateFeature {

    @Override
    public void populate(ChunkGenerateContext context, NukkitRandom random) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int x = random.nextBoundedInt(15);
        int z = random.nextBoundedInt(15);
        int y = context.getChunk().getHeightMap(x, z);
        Position position = new Position((chunkX << 4) + x, y+1, (chunkZ << 4) + z, chunk.getLevel());
        while(!isSupportValid(chunk.getBlockState(x, y, z).toBlock(position)) && y > SEA_LEVEL) {
            y--;
        }
        position.setY(y + 1);
        if (y >= SEA_LEVEL && isSupportValid(chunk.getBlockState(x, y, z).toBlock(position))) {
            BlockManager manager = new BlockManager(chunk.getLevel());
            BlockManager object = new BlockManager(chunk.getLevel());
            if(!manager.getBlockAt(x, y, z).isAir()) return;
            place(object, position.getFloorX(), position.getFloorY(), position.getFloorZ());
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
        return support.is(BlockTags.DIRT);
    }

}
