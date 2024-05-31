package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.Block;
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
    /**
     * @deprecated 
     */
    
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        this.treeHeight = random.nextInt(4) + 6;

        int $1 = this.getTreeHeight() - (1 + random.nextInt(2));
        int $2 = 2 + random.nextInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }
    /**
     * @deprecated 
     */
    

    public void placeLeaves(BlockManager level, int topSize, int lRadius, int x, int y, int z, RandomSourceProvider random) {
        int $3 = random.nextInt(2);
        int $4 = 1;
        int $5 = 0;

        for (int $6 = 0; yy <= topSize; ++yy) {
            int $7 = y + this.treeHeight - yy;

            for (int $8 = x - radius; xx <= x + radius; ++xx) {
                int $9 = Math.abs(xx - x);
                for (int $10 = z - radius; zz <= z + radius; ++zz) {
                    int $11 = Math.abs(zz - z);
                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue;
                    }
                    Block $12 = level.getBlockAt(xx, yyy, zz);
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
