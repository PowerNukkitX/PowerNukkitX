package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class LegacyNetherTree extends LegacyTreeGenerator {
    protected int treeHeight;

    public LegacyNetherTree() {
        this(RandomSourceProvider.create().nextInt(9) + 4);
    }

    public LegacyNetherTree(int treeHeight) {
        this.treeHeight = treeHeight;
    }

    @Override
    public int getTreeHeight() {
        return treeHeight;
    }

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
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        if (checkY(level, y)) { // 防止长出下界顶部基岩层
            return;
        }

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight());

        double blankArea = -3;
        int mid = (int) (1 - blankArea / 2);
        for (int yy = y - 3 + treeHeight; yy <= y + this.treeHeight - 1; ++yy) {
            if (checkY(level, yy)) { // 防止长出下界顶部基岩层
                continue;
            }

            for (int xx = x - mid; xx <= x + mid; xx++) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; zz += mid * 2) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && random.nextInt(2) == 0) {
                        continue;
                    }
                    Block block = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(20) == 0) level.setBlockStateAt(xx, yy, zz, Block.SHROOMLIGHT);
                        else level.setBlockStateAt(xx, yy, zz, this.getLeafBlockState());
                    }
                }
            }

            for (int zz = z - mid; zz <= z + mid; zz++) {
                int zOff = Math.abs(zz - z);
                for (int xx = x - mid; xx <= x + mid; xx += mid * 2) {
                    int xOff = Math.abs(xx - x);
                    if (xOff == mid && zOff == mid && (random.nextInt(2) == 0)) {
                        continue;
                    }
                    Block block = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(20) == 0) level.setBlockStateAt(xx, yy, zz, Block.SHROOMLIGHT);
                        else level.setBlockStateAt(xx, yy, zz, this.getLeafBlockState());
                    }
                }
            }
        }

        for (int yy = y - 4 + treeHeight; yy <= y + this.treeHeight - 3; ++yy) {
            if (checkY(level, yy)) { // 防止长出下界顶部基岩层
                continue;
            }

            for (int xx = x - mid; xx <= x + mid; xx++) {
                for (int zz = z - mid; zz <= z + mid; zz += mid * 2) {
                    Block block = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(3) == 0) {
                            for (int i = 0; i < random.nextInt(5); i++) {
                                Block block2 = level.getBlockAt(xx, yy - i, zz);
                                if (!block2.isSolid())
                                    level.setBlockStateAt(xx, yy - i, zz, getLeafBlockState());
                            }
                        }
                    }
                }
            }

            for (int zz = z - mid; zz <= z + mid; zz++) {
                for (int xx = x - mid; xx <= x + mid; xx += mid * 2) {
                    Block block = level.getBlockAt(xx, yy, zz);
                    if (!block.isSolid()) {
                        if (random.nextInt(3) == 0) {
                            for (int i = 0; i < random.nextInt(4); i++) {
                                Block block2 = level.getBlockAt(xx, yy - i, zz);
                                if (!block2.isSolid())
                                    level.setBlockStateAt(xx, yy - i, zz, getLeafBlockState());
                            }
                        }
                    }
                }
            }
        }

        for (int xCanopy = x - mid + 1; xCanopy <= x + mid - 1; xCanopy++) {
            for (int zCanopy = z - mid + 1; zCanopy <= z + mid - 1; zCanopy++) {
                Block block = level.getBlockAt(xCanopy, y + treeHeight, zCanopy);
                if (!block.isSolid())
                    level.setBlockStateAt(xCanopy, y + treeHeight, zCanopy, getLeafBlockState());
            }
        }
    }

    @Override
    protected void placeTrunk(BlockManager level, int x, int y, int z, RandomSourceProvider random, int trunkHeight) {
        level.setBlockStateAt(x, y, z, getTrunkBlockState());
        for (int yy = 0; yy < trunkHeight; ++yy) {
            if (checkY(level, y + yy)) { // 防止长出下界顶部基岩层
                continue;
            }
            Block b = level.getBlockAt(x, y + yy, z);
            if (this.overridable(b)) {
                level.setBlockStateAt(x, y + yy, z, this.getTrunkBlockState());
            }
        }
    }
}
