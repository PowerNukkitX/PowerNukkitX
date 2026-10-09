package org.powernukkitx.level.generator.feature.decoration;

import org.powernukkitx.block.*;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.tags.BlockTags;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class ForestFlowerFoliageFeature extends GenerateFeature {

    public static final String NAME = "minecraft:forest_first_foliage_feature";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());

        int count = random.nextInt(5) - 3;
        if (count <= 0) return;

        BlockManager object = new BlockManager(level);
        LongOpenHashSet occupied = new LongOpenHashSet();

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int i = 0; i < count; i++) {
            BlockState lowerState;
            BlockState upperState;

            switch (random.nextInt(3)) {
                case 0 -> {
                    lowerState = BlockLilac.PROPERTIES.getDefaultState();
                    upperState = BlockLilac.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true));
                }
                case 1 -> {
                    lowerState = BlockRoseBush.PROPERTIES.getDefaultState();
                    upperState = BlockRoseBush.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true));
                }
                default -> {
                    lowerState = BlockPeony.PROPERTIES.getDefaultState();
                    upperState = BlockPeony.PROPERTIES.getBlockState(CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true));
                }
            }

            for (int center = 0; center < 5; center++) {
                int localX = random.nextInt(16);
                int localZ = random.nextInt(16);

                int heightBound = chunk.getHeightMap(localX, localZ) + 31;
                if (heightBound <= 0) continue;

                int centerX = baseX + localX;
                int centerY = random.nextInt(heightBound);
                int centerZ = baseZ + localZ;

                scatterDoublePlant(chunk, level, object, occupied, centerX, centerY, centerZ, lowerState, upperState);
            }
        }

        queueObject(chunk, object);
    }

    private void scatterDoublePlant(IChunk sourceChunk, Level level, BlockManager object, LongOpenHashSet occupied, int centerX, int centerY, int centerZ, BlockState lowerState, BlockState upperState) {
        int minHeight = level.getMinHeight();
        int maxHeight = level.getMaxHeight();

        for (int i = 0; i < 64; i++) {
            int z = centerZ + ((random.nextInt() & 7) - (random.nextInt() & 7));
            int y = centerY + ((random.nextInt() & 3) - (random.nextInt() & 3));
            int x = centerX + ((random.nextInt() & 7) - (random.nextInt() & 7));

            if (y <= minHeight || y + 1 >= maxHeight) continue;

            long lowerKey = blockKey(x, y, z);
            long upperKey = blockKey(x, y + 1, z);
            long supportKey = blockKey(x, y - 1, z);

            if (occupied.contains(lowerKey) || occupied.contains(upperKey) || occupied.contains(supportKey)) continue;

            int targetChunkX = x >> 4;
            int targetChunkZ = z >> 4;

            IChunk targetChunk;
            if (targetChunkX == sourceChunk.getX() && targetChunkZ == sourceChunk.getZ()) {
                targetChunk = sourceChunk;
            } else {
                targetChunk = level.getChunkIfLoaded(targetChunkX, targetChunkZ);
            }

            if (targetChunk == null) continue;

            int localX = x & 15;
            int localZ = z & 15;

            if (targetChunk.getBlockState(localX, y, localZ) != BlockAir.STATE) continue;
            if (targetChunk.getBlockState(localX, y + 1, localZ) != BlockAir.STATE) continue;

            BlockState supportState = targetChunk.getBlockState(localX, y - 1, localZ);
            if (!supportState.toBlock().hasTag(BlockTags.DIRT)) continue;

            object.setBlockStateAt(x, y, z, lowerState);
            object.setBlockStateAt(x, y + 1, z, upperState);

            occupied.add(lowerKey);
            occupied.add(upperKey);
        }
    }

    private static long blockKey(int x, int y, int z) {
        return ((long) (x & 0x3ffffff) << 38) | ((long) (z & 0x3ffffff) << 12) | (y & 0xfffL);
    }

    @Override
    public String name() {
        return NAME;
    }
}
