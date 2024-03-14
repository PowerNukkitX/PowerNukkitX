package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSourceProvider;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class LegacyBigSpruceTree extends LegacySpruceTree {
    private final float leafStartHeightMultiplier;
    private final int baseLeafRadius;

    public LegacyBigSpruceTree(float leafStartHeightMultiplier, int baseLeafRadius) {
        this.leafStartHeightMultiplier = leafStartHeightMultiplier;
        this.baseLeafRadius = baseLeafRadius;
    }

    public void setRandomTreeHeight(RandomSourceProvider random) {
        this.treeHeight = random.nextInt(15) + 20;
    }

    @Override
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        if (this.treeHeight == 0) {
            this.setRandomTreeHeight(random);
        }

        int topSize = this.treeHeight - (int) (this.treeHeight * leafStartHeightMultiplier);
        int lRadius = baseLeafRadius + random.nextInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    @Override
    protected void placeTrunk(BlockManager level, int x, int y, int z, RandomSourceProvider random, int trunkHeight) {
        // The base dirt block
        level.setBlockStateAt(x, y - 1, z, Block.DIRT);
        int radius = 2;

        for (int yy = 0; yy < trunkHeight; ++yy) {
            for (int xx = 0; xx < radius; xx++) {
                for (int zz = 0; zz < radius; zz++) {
                    Block b = level.getBlockAt(x + xx, y + yy, z + zz);
                    if (this.overridable(b)) {
                        level.setBlockStateAt(x + xx, y + yy, z + zz, getTrunkBlockState());
                    }
                }
            }
        }
    }

    public void placeLeaves(BlockManager level, int topSize, int lRadius, int x, int y, int z, RandomSourceProvider random) {
        int radius = random.nextInt(2);
        int maxR = 1;
        int minR = 0;

        for (int yy = 0; yy <= topSize; ++yy) {
            int yyy = y + this.treeHeight - yy;

            for (int xx = x - radius; xx <= x + radius; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - radius; zz <= z + radius; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue;
                    }
                    boolean solid = level.getBlockAt(xx, yyy, zz).isSolid();
                    if (!solid) {
                        level.setBlockStateAt(xx, yyy, zz, getLeafBlockState());
                    }
                }
            }

            if (radius >= maxR) {
                radius = minR;
                minR = 1;
                if (++maxR > lRadius) {
                    maxR = lRadius;
                }
            } else {
                ++radius;
            }
        }
    }
}
