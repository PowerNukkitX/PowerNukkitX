package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class GroupedDiscFeature extends CountGenerateFeature {

    public abstract BlockState getSourceBlock();
    public abstract int getMinRadius();
    public abstract int getMaxRadius();

    public double getProbability() {
        return 1d;
    }

    @Override
    public int getBase() {
        return 0;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int randomX = random.nextInt(15);
        int randomZ = random.nextInt(15);
        int height = getY(chunk, randomX, randomZ);
        int sourceX = (chunkX << 4) + randomX;
        int sourceZ = (chunkZ << 4) + randomZ;
        BlockState topBlockState = chunk.getBlockState(randomX, height+1, randomZ);
        if(topBlockState == BlockAir.STATE) {
            BlockManager object = new BlockManager(level);
            int radius = NukkitMath.randomRange(random, getMinRadius(), getMaxRadius());
            for (int x = sourceX - radius; x <= sourceX + radius; x++) {
                for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                    if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= radius * radius) {
                        if (random.nextDouble() >= getProbability()) {
                            continue;
                        }
                        Vector3 p = new Vector3(x, level.getHeightMap(x, z)+1, z);
                        if(isSupportValid(level.getBlock(p.down()))) {
                            object.setBlockStateAt(p, getSourceBlock());
                        }
                    }
                }
            }
            queueObject(chunk, object);
        }
    }

    public boolean isSupportValid(Block block) {
        return block.is(BlockTags.DIRT) || block.is(BlockTags.SAND);
    }

    public int getY(IChunk chunk, int x, int z) {
        return chunk.getHeightMap(x, z);
    }

}
