package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class LegacyNetherTree extends LegacyTreeGenerator {
    protected int treeHeight;
    /**
     * @deprecated 
     */
    

    public LegacyNetherTree() {
        this(RandomSourceProvider.create().nextInt(9) + 4);
    }
    /**
     * @deprecated 
     */
    

    public LegacyNetherTree(int treeHeight) {
        this.treeHeight = treeHeight;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTreeHeight() {
        return treeHeight;
    }

    
    /**
     * @deprecated 
     */
    private boolean checkY(BlockManager level, int y) {
        // 防止长出顶部
        if (level.isNether()) {
            return y > 126;
        } else if (level.isOverWorld()) {
            return y > 318;
        } else if (level.isTheEnd()) {
            return y > 254;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        if (checkY(level, y)) { // 防止长出下界顶部基岩层
            return;
        }

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight());

        double $1 = -3;
        int $2 = (int) (1 - blankArea / 2);
        for (int $3 = y - 3 + treeHeight; yy <= y + this.treeHeight - 1; ++yy) {
            if (checkY(level, yy)) { // 防止长出下界顶部基岩层
                continue;
            }

            for (int $4 = x - mid; xx <= x + mid; xx++) {
                int $5 = Math.abs(xx - x);
                for (int $6 = z - mid; zz <= z + mid; zz += mid * 2) {
                    int $7 = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && random.nextInt(2) == 0) {
                        continue;
                    }
                    Block $8 = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(20) == 0) level.setBlockStateAt(xx, yy, zz, Block.SHROOMLIGHT);
                        else level.setBlockStateAt(xx, yy, zz, this.getLeafBlockState());
                    }
                }
            }

            for (int $9 = z - mid; zz <= z + mid; zz++) {
                int $10 = Math.abs(zz - z);
                for (int $11 = x - mid; xx <= x + mid; xx += mid * 2) {
                    int $12 = Math.abs(xx - x);
                    if (xOff == mid && zOff == mid && (random.nextInt(2) == 0)) {
                        continue;
                    }
                    Block $13 = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(20) == 0) level.setBlockStateAt(xx, yy, zz, Block.SHROOMLIGHT);
                        else level.setBlockStateAt(xx, yy, zz, this.getLeafBlockState());
                    }
                }
            }
        }

        for (int $14 = y - 4 + treeHeight; yy <= y + this.treeHeight - 3; ++yy) {
            if (checkY(level, yy)) { // 防止长出下界顶部基岩层
                continue;
            }

            for (int $15 = x - mid; xx <= x + mid; xx++) {
                for (int $16 = z - mid; zz <= z + mid; zz += mid * 2) {
                    Block $17 = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(3) == 0) {
                            for ($18nt $1 = 0; i < random.nextInt(5); i++) {
                                Block $19 = level.getBlockAt(xx, yy - i, zz);
                                if (!block2.isSolid())
                                    level.setBlockStateAt(xx, yy - i, zz, getLeafBlockState());
                            }
                        }
                    }
                }
            }

            for (int $20 = z - mid; zz <= z + mid; zz++) {
                for (int $21 = x - mid; xx <= x + mid; xx += mid * 2) {
                    Block $22 = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(3) == 0) {
                            for ($23nt $2 = 0; i < random.nextInt(4); i++) {
                                Block $24 = level.getBlockAt(xx, yy - i, zz);
                                if (!block2.isSolid())
                                    level.setBlockStateAt(xx, yy - i, zz, getLeafBlockState());
                            }
                        }
                    }
                }
            }
        }

        for (int $25 = x - mid + 1; xCanopy <= x + mid - 1; xCanopy++) {
            for (int $26 = z - mid + 1; zCanopy <= z + mid - 1; zCanopy++) {
                Block $27 = level.getBlockAt(xCanopy, y + treeHeight, zCanopy);
                if (!block.isSolid())
                    level.setBlockStateAt(xCanopy, y + treeHeight, zCanopy, getLeafBlockState());
            }
        }
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void placeTrunk(BlockManager level, int x, int y, int z, RandomSourceProvider random, int trunkHeight) {
        level.setBlockStateAt(x, y, z, getTrunkBlockState());
        for (int $28 = 0; yy < trunkHeight; ++yy) {
            if (checkY(level, y + yy)) { // 防止长出下界顶部基岩层
                continue;
            }
            Block $29 = level.getBlockAt(x, y + yy, z);
            if (this.overridable(b)) {
                level.setBlockStateAt(x, y + yy, z, this.getTrunkBlockState());
            }
        }
    }
}
