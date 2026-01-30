package cn.nukkit.level.generator.feature.river;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class DiscGenerateFeature extends CountGenerateFeature {

    public static final BlockState STATE_STILL_WATER = BlockWater.PROPERTIES.getDefaultState();

    protected static final BlockState STATE_STONE = BlockStone.PROPERTIES.getDefaultState();
    protected static final BlockState STATE_DIRT = BlockDirt.PROPERTIES.getDefaultState();
    protected static final BlockState STATE_GRASS = BlockGrassBlock.PROPERTIES.getDefaultState();

    protected static final BlockState[] REPLACEMENTS = new BlockState[] { STATE_STONE, STATE_DIRT, STATE_GRASS };

    public abstract BlockState getSourceBlock();
    public abstract int getMinRadius();
    public abstract int getMaxRadius();
    public abstract int getRadiusY();

    public double getProbability() {
        return 1d;
    }
    public BlockState[] getReplacementBlocks() {
        return REPLACEMENTS;
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
        if (random.nextDouble() >= getProbability()) {
            return;
        }
        int randomX = random.nextInt(15);
        int randomZ = random.nextInt(15);
        int height = chunk.getHeightMap(randomX, randomZ);
        int sourceX = (chunkX << 4) + randomX;
        int sourceZ = (chunkZ << 4) + randomZ;
        BlockState topBlockState = chunk.getBlockState(randomX, height, randomZ);
        if(topBlockState == STATE_STILL_WATER) {
            int depth = 0;
            while (topBlockState == BlockWater.PROPERTIES.getBlockState()) {
                topBlockState = chunk.getBlockState(randomX, height - (++depth), randomZ);
            }
            int sourceY = height - (++depth);

            if (sourceY < getRadiusY()) {
                return;
            }
            BlockManager object = new BlockManager(level);
            int radius = NukkitMath.randomRange(random, getMinRadius(), getMaxRadius());
            for (int x = sourceX - radius; x <= sourceX + radius; x++) {
                for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                    if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= radius * radius) {
                        for (int y = sourceY - getRadiusY(); y <= sourceY + getRadiusY(); y++) {
                            for (BlockState replaceBlockState : getReplacementBlocks()) {
                                if (object.getBlockIfCachedOrLoaded(x, y, z).getBlockState().equals(replaceBlockState)) {
                                    object.setBlockStateAt(x, y, z, getSourceBlock());
                                }
                            }
                        }
                    }
                }
            }
            queueObject(chunk, object);
        }
    }
}
