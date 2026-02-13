package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public abstract class SurfaceGenerateFeature extends CountGenerateFeature {

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
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
            if(!manager.getBlockIfCachedOrLoaded(x, y, z).isAir()) return;
            place(object, position.getFloorX(), position.getFloorY(), position.getFloorZ());
            queueObject(chunk, object);
        }
    }

    public abstract void place(BlockManager manager, int x, int y, int z);

    public boolean isSupportValid(Block support) {
        return support.hasTag(BlockTags.DIRT);
    }

}
