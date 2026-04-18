package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSeaPickle;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.block.property.CommonBlockProperties.CLUSTER_COUNT;
import static cn.nukkit.level.generator.feature.river.DiscGenerateFeature.STATE_STILL_WATER;

public class SeaPickleFeature extends CountGenerateFeature {
    public static final String NAME = "minecraft:pickle_feature";

    @Override
    public int getBase() {
        return 1;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        int originX = random.nextInt(15);
        int originZ = random.nextInt(15);
        int placed = 0;
        int tries = 20;

        for (int i = 0; i < tries; i++) {
            int x = originX + random.nextInt(8) - random.nextInt(8);
            int z = originZ + random.nextInt(8) - random.nextInt(8);
            if (x < 0 || x > 15 || z < 0 || z > 15) {
                continue;
            }

            int y = findOceanFloorWaterY(chunk, x, z);
            if (y <= chunk.getLevel().getMinHeight() || y >= chunk.getLevel().getMaxHeight()) {
                continue;
            }

            if (!isWater(chunk.getBlockState(x, y, z)) || !chunk.getBlockState(x, y - 1, z).toBlock().isSolid()) {
                continue;
            }

            BlockState seaPickle = BlockSeaPickle.PROPERTIES.getBlockState(CLUSTER_COUNT.createValue(random.nextInt(4)));
            chunk.setBlockState(x, y, z, seaPickle);
            chunk.setBlockState(x, y, z, STATE_STILL_WATER, 1);
            placed++;
        }

        if (placed == 0) {
            int y = findOceanFloorWaterY(chunk, originX, originZ);
            if (y > chunk.getLevel().getMinHeight() && y < chunk.getLevel().getMaxHeight()
                    && isWater(chunk.getBlockState(originX, y, originZ))
                    && chunk.getBlockState(originX, y - 1, originZ).toBlock().isSolid()) {
                BlockState seaPickle = BlockSeaPickle.PROPERTIES.getBlockState(CLUSTER_COUNT.createValue(random.nextInt(4)));
                chunk.setBlockState(originX, y, originZ, seaPickle);
                chunk.setBlockState(originX, y, originZ, STATE_STILL_WATER, 1);
            }
        }
    }

    private static int findOceanFloorWaterY(IChunk chunk, int x, int z) {
        int y = chunk.getHeightMap(x, z);
        Level level = chunk.getLevel();
        if (y <= level.getMinHeight()) {
            return y;
        }
        while (y > level.getMinHeight() && isWater(chunk.getBlockState(x, y, z))) {
            y--;
        }
        return y + 1;
    }

    private static boolean isWater(BlockState state) {
        String id = state.getIdentifier();
        return BlockID.WATER.equals(id) || BlockID.FLOWING_WATER.equals(id);
    }
}
