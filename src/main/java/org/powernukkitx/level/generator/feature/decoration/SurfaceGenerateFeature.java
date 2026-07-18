package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.feature.CountGenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.tags.BlockTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public abstract class SurfaceGenerateFeature extends CountGenerateFeature {

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int x = random.nextBoundedInt(15);
        int z = random.nextBoundedInt(15);
        int y = context.getChunk().getHeightMap(x, z);
        int worldX = (chunkX << 4) + x;
        int worldZ = (chunkZ << 4) + z;
        Position position = new Position(worldX, y, worldZ, chunk.getLevel());
        while(!isSupportValid(chunk.getBlockState(x, y, z).toBlock(position)) && y >= SEA_LEVEL - 1) {
            y--;
            position.setY(y);
        }
        if (y >= SEA_LEVEL - 1 && isSupportValid(chunk.getBlockState(x, y, z).toBlock(position))) {
            BlockManager manager = new BlockManager(chunk.getLevel());
            BlockManager object = new BlockManager(chunk.getLevel());
            if(!manager.getBlockIfCachedOrLoaded(worldX, y + 1, worldZ).isAir()) return;
            place(object, worldX, y + 1, worldZ);
            queueObject(chunk, object);
        }
    }

    public abstract void place(BlockManager manager, int x, int y, int z);

    public boolean isSupportValid(Block support) {
        return support.hasTag(BlockTags.DIRT);
    }

}
