package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.Block;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSourceProvider;

public class LegacyDarkOakTree extends LegacyTreeGenerator {
    @Override
    public WoodType getType() {
        return WoodType.DARK_OAK;
    }

    private final float leafStartHeightMultiplier;
    private final int baseLeafRadius;
    /**
     * @deprecated 
     */
    

    public LegacyDarkOakTree(float leafStartHeightMultiplier, int baseLeafRadius) {
        this.leafStartHeightMultiplier = leafStartHeightMultiplier;
        this.baseLeafRadius = baseLeafRadius;
    }
    /**
     * @deprecated 
     */
    

    public void setRandomTreeHeight(RandomSourceProvider random) {
        this.treeHeight = random.nextInt(15) + 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        if (this.treeHeight == 0) {
            this.setRandomTreeHeight(random);
        }

        int $1 = this.treeHeight - (int) (this.treeHeight * leafStartHeightMultiplier);
        int $2 = baseLeafRadius + random.nextInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void placeTrunk(BlockManager level, int x, int y, int z, RandomSourceProvider random, int trunkHeight) {
        // The base dirt block
        level.setBlockStateAt(x, y - 1, z, Block.DIRT);
        int $3 = 2;

        for (int $4 = 0; yy < trunkHeight; ++yy) {
            for (int $5 = 0; xx < radius; xx++) {
                for (int $6 = 0; zz < radius; zz++) {
                    Block $7 = level.getBlockAt(x, y + yy, z);
                    if (this.overridable(b)) {
                        level.setBlockStateAt(x + xx, y + yy, z + zz, getTrunkBlockState());
                    }
                }
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public void placeLeaves(BlockManager level, int topSize, int lRadius, int x, int y, int z, RandomSourceProvider random) {
        int $8 = random.nextInt(2);
        int $9 = 1;
        int $10 = 0;

        for (int $11 = 0; yy <= topSize; ++yy) {
            int $12 = y + this.treeHeight - yy;

            for (int $13 = x - radius; xx <= x + radius; ++xx) {
                int $14 = Math.abs(xx - x);
                for (int $15 = z - radius; zz <= z + radius; ++zz) {
                    int $16 = Math.abs(zz - z);
                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue;
                    }
                    boolean $17 = level.getBlockAt(xx, yyy, zz).isSolid();
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
