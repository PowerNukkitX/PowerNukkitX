package cn.nukkit.level.generator.feature.river;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrassBlock;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.UnsafeChunk;
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
        if (topBlockState == STATE_STILL_WATER) {
            BlockState stillWaterState = BlockWater.PROPERTIES.getBlockState();
            int depth = 0;
            while (topBlockState == stillWaterState) {
                topBlockState = chunk.getBlockState(randomX, height - (++depth), randomZ);
            }
            int sourceY = height - (++depth);
            int radiusY = getRadiusY();
            if (sourceY < radiusY) {
                return;
            }

            BlockManager object = new BlockManager(level);
            BlockState sourceBlock = getSourceBlock();
            BlockState[] replacementBlocks = getReplacementBlocks();
            int radius = NukkitMath.randomRange(random, getMinRadius(), getMaxRadius());
            int radiusSquared = radius * radius;
            int minY = sourceY - radiusY;
            int maxY = sourceY + radiusY;
            int minX = sourceX - radius;
            int maxX = sourceX + radius;
            int minZ = sourceZ - radius;
            int maxZ = sourceZ + radius;
            int minChunkX = minX >> 4;
            int maxChunkX = maxX >> 4;
            int minChunkZ = minZ >> 4;
            int maxChunkZ = maxZ >> 4;
            boolean[] placedAny = {false};

            for (int currentChunkX = minChunkX; currentChunkX <= maxChunkX; currentChunkX++) {
                int chunkStartX = currentChunkX << 4;
                int chunkEndX = chunkStartX + 15;
                int startX = Math.max(minX, chunkStartX);
                int endX = Math.min(maxX, chunkEndX);
                for (int currentChunkZ = minChunkZ; currentChunkZ <= maxChunkZ; currentChunkZ++) {
                    int chunkStartZ = currentChunkZ << 4;
                    int chunkEndZ = chunkStartZ + 15;
                    int startZ = Math.max(minZ, chunkStartZ);
                    int endZ = Math.min(maxZ, chunkEndZ);
                    IChunk targetChunk = level.getChunkIfLoaded(currentChunkX, currentChunkZ);
                    if(targetChunk == null) continue;
                    UnsafeChunk unsafeChunk = new UnsafeChunk((Chunk) targetChunk);
                    for (int x = startX; x <= endX; x++) {
                        int dx = x - sourceX;
                        int dx2 = dx * dx;
                        int localX = x & 15;
                        for (int z = startZ; z <= endZ; z++) {
                            int dz = z - sourceZ;
                            if (dx2 + dz * dz > radiusSquared) {
                                continue;
                            }
                            int localZ = z & 15;
                            for (int y = minY; y <= maxY; y++) {
                                BlockState currentState = unsafeChunk.getBlockState(localX, y, localZ, 0);
                                for (BlockState replaceBlockState : replacementBlocks) {
                                    if (currentState.equals(replaceBlockState)) {
                                        object.setBlockStateAt(x, y, z, sourceBlock);
                                        placedAny[0] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (placedAny[0]) {
                queueObject(chunk, object);
            }
        }
    }
}
