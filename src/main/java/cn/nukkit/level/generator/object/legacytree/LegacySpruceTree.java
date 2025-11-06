package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSourceProvider;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LegacySpruceTree extends LegacyTreeGenerator {
    @Override
    public WoodType getType() {
        return WoodType.SPRUCE;
    }

    @Override
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        this.treeHeight = random.nextInt(4) + 6;

        int topSize = this.getTreeHeight() - (1 + random.nextInt(2));
        int lRadius = 2 + random.nextInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    public void placeLeaves(BlockManager level, int topSize, int lRadius, int x, int y, int z, RandomSourceProvider random) {
        int radius = random.nextInt(2);
        int maxR = 1;
        int minR = 0;
        level.setBlockStateAt(x, y-1, z, BlockDirt.PROPERTIES.getDefaultState());
        for (int yy = 0; yy <= topSize; ++yy) {
            int yyy = y + this.treeHeight - yy;

            for (int xx = x - radius; xx <= x + radius; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - radius; zz <= z + radius; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue;
                    }
                    Block blockAt = level.getBlockIfCachedOrLoaded(xx, yyy, zz);
                    if (!blockAt.isSolid()) {
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
