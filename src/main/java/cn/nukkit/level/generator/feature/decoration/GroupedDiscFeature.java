package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.UnsafeChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Set;

public abstract class GroupedDiscFeature extends CountGenerateFeature {
    private static final Set<String> DIRT_BLOCK_IDS = BlockTags.getBlockSet(BlockTags.DIRT);
    private static final Set<String> SAND_BLOCK_IDS = BlockTags.getBlockSet(BlockTags.SAND);


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
        double probability = getProbability();
        boolean alwaysPlace = probability >= 1d;
        BlockState topBlockState = chunk.getBlockState(randomX, height + 1, randomZ);
        if (topBlockState == BlockAir.STATE) {
            BlockManager object = new BlockManager(level);
            BlockState sourceBlock = getSourceBlock();
            int radius = NukkitMath.randomRange(random, getMinRadius(), getMaxRadius());
            int radiusSquared = radius * radius;
            int minX = sourceX - radius;
            int maxX = sourceX + radius;
            int minZ = sourceZ - radius;
            int maxZ = sourceZ + radius;
            int minChunkX = minX >> 4;
            int maxChunkX = maxX >> 4;
            int minChunkZ = minZ >> 4;
            int maxChunkZ = maxZ >> 4;
            boolean placedAny = false;

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
                    if (targetChunk == null) {
                        continue;
                    }
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
                            if (!alwaysPlace && random.nextDouble() >= probability) {
                                continue;
                            }
                            int localZ = z & 15;
                            int placeY = unsafeChunk.getHeightMap(localX, localZ) + 1;
                            BlockState supportState = unsafeChunk.getBlockState(localX, placeY - 1, localZ, 0);
                            if (isSupportValid(supportState)) {
                                object.setBlockStateAt(x, placeY, z, sourceBlock);
                                placedAny = true;
                            }
                        }
                    }
                }
            }

            if (placedAny) {
                queueObject(chunk, object);
            }
        }
    }

    public boolean isSupportValid(Block block) {
        return block.hasTag(BlockTags.DIRT) || block.hasTag(BlockTags.SAND);
    }

    protected boolean isSupportValid(BlockState blockState) {
        String identifier = blockState.getIdentifier();
        return DIRT_BLOCK_IDS.contains(identifier) || SAND_BLOCK_IDS.contains(identifier);
    }

    public int getY(IChunk chunk, int x, int z) {
        return chunk.getHeightMap(x, z);
    }

}
