package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBamboo;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitRandom;

/**
 * @author GoodLucky777
 */
public class PopulatorBamboo extends PopulatorCount {

    private static final BlockState STATE_PODZOL = BlockState.of(PODZOL);
    // TODO: Use BlockState if BlockBamboo implement BlockState
    private static final BlockBamboo BLOCK_BAMBOO = new BlockBamboo();
    private static final Block BLOCK_BAMBOO_DEFAULT = BLOCK_BAMBOO.clone();

    static {
        ((BlockBamboo) BLOCK_BAMBOO_DEFAULT).setThick(true);
    }

    private static final Block BLOCK_BAMBOO_LEAF_SMALL = BLOCK_BAMBOO_DEFAULT.clone();
    private static final Block BLOCK_BAMBOO_LEAF_LARGE = BLOCK_BAMBOO_DEFAULT.clone();

    static {
        ((BlockBamboo) BLOCK_BAMBOO_LEAF_SMALL).setLeafSize(BlockBamboo.LEAF_SIZE_SMALL);
        ((BlockBamboo) BLOCK_BAMBOO_LEAF_LARGE).setLeafSize(BlockBamboo.LEAF_SIZE_LARGE);
    }

    private static final Block BLOCK_BAMBOO_LEAF_LARGE_AGED = BLOCK_BAMBOO_LEAF_LARGE.clone();

    static {
        ((BlockBamboo) BLOCK_BAMBOO_LEAF_LARGE_AGED).setAge(1);
    }

    /**
     * 灰化土生成概率
     */
    private double podzolProbability = 0.6;

    private boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && (EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk) || EnsureBelow.ensureBelow(x, y, z, DIRT, chunk) || EnsureBelow.ensureBelow(x, y, z, PODZOL, chunk));
    }

    private void generateBamboo(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        final int height = getMaxHeight(level, x, y, z, random.nextBoundedInt(12) + 5);

        for (int i = 0; i < height; i++) {
            if (i > (height - 3) && height >= 3) {
                if (i > (height - 2)) {
                    if (i == (height - 1)) {
                        level.setBlockStateAt(x, y + i, z, BlockState.of(BLOCK_BAMBOO_LEAF_LARGE_AGED.getId(), BLOCK_BAMBOO_LEAF_LARGE_AGED.getDamage()));
                    } else {
                        level.setBlockStateAt(x, y + i, z, BlockState.of(BLOCK_BAMBOO_LEAF_LARGE.getId(), BLOCK_BAMBOO_LEAF_LARGE.getDamage()));
                    }
                } else {
                    level.setBlockStateAt(x, y + i, z, BlockState.of(BLOCK_BAMBOO_LEAF_SMALL.getId(), BLOCK_BAMBOO_LEAF_SMALL.getDamage()));
                }
            } else {
                level.setBlockStateAt(x, y + i, z, BlockState.of(BLOCK_BAMBOO_DEFAULT.getId(), BLOCK_BAMBOO_DEFAULT.getDamage()));
            }
        }
    }

    private void generatePodzol(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int radius = random.nextBoundedInt(4) + 1;

        for (int podzolX = x - radius; podzolX <= x + radius; podzolX++) {
            for (int podzolZ = z - radius; podzolZ <= z + radius; podzolZ++) {
                if ((podzolX - x) * (podzolX - x) + (podzolZ - z) * (podzolZ - z) <= radius * radius) {
                    int checkId = level.getBlockIdAt(x, y - 1, z);

                    if (checkId == GRASS || checkId == DIRT) {
                        level.setBlockStateAt(podzolX, y - 1, podzolZ, STATE_PODZOL);
                    }
                }
            }
        }
    }

    private int getMaxHeight(ChunkManager level, int x, int y, int z, int height) {
        int maxHeight = 0;
        for (int i = 0; i < height; i++) {
            if (level.getBlockIdAt(x, (y + i), z) == AIR) {
                maxHeight++;
            } else {
                break;
            }
        }

        return maxHeight;
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        int y;
        for (y = 254; y >= 0; --y) {
            if (!PopulatorHelpers.isNonSolid(chunk.getBlockId(x, y, z))) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }

    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = random.nextBoundedInt(16);
        int z = random.nextBoundedInt(16);
        int y = getHighestWorkableBlock(level, x, z, chunk);

        if (y > 0 && canStay(x, y, z, chunk)) {
            x += (chunkX << 4);
            z += (chunkZ << 4);

            if (random.nextDouble() < this.podzolProbability) {
                generatePodzol(level, x, y, z, random);
            }

            generateBamboo(level, x, y, z, random);
        }
    }

    public void setPodzolProbability(double podzolProbability) {
        this.podzolProbability = podzolProbability;
    }
}
