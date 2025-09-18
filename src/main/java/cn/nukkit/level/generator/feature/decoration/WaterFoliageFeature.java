package cn.nukkit.level.generator.feature.decoration;


import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.feature.river.DiscGenerateFeature.STATE_STILL_WATER;

public abstract class WaterFoliageFeature extends CountGenerateFeature {

    protected abstract boolean canStay(int x, int y, int z, IChunk chunk);

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int randomX = random.nextInt(15);
        int randomZ = random.nextInt(15);
        int height = chunk.getHeightMap(randomX, randomZ);
        int x = (chunkX << 4) + randomX;
        int z = (chunkZ << 4) + randomZ;
        BlockState topBlockState = chunk.getBlockState(randomX, height, randomZ);
        if(topBlockState == STATE_STILL_WATER) {
            int depth = 0;
            while (topBlockState == BlockWater.PROPERTIES.getBlockState()) {
                topBlockState = chunk.getBlockState(randomX, height - (++depth), randomZ);
            }
            int y = (height - depth) + 1;
            if (y > 0 && canStay(randomX, y, randomZ, chunk)) {
                placeBlock(randomX, y, randomZ, chunk, random);
            }
        }
    }

    protected abstract void placeBlock(int x, int y, int z, IChunk chunk, RandomSourceProvider random);

}
