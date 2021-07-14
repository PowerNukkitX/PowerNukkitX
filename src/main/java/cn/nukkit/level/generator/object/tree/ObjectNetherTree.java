package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public abstract class ObjectNetherTree extends ObjectTree {
    protected int treeHeight;

    public ObjectNetherTree() {
        this(new NukkitRandom().nextBoundedInt(9)+4);
    }

    public ObjectNetherTree(int treeHeight) {
        this.treeHeight = treeHeight;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getTreeHeight() {
        return treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.placeTrunk(level, x, y, z, random, this.getTreeHeight());

        double blankArea = -3;
        int mid = (int) (1 - blankArea / 2);
        for (int yy = y - 3 + treeHeight; yy <= y + this.treeHeight-1; ++yy) {
            for (int xx = x - mid; xx <= x + mid; xx++) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; zz += mid*2) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && random.nextBoundedInt(2) == 0) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {
                        if(random.nextBoundedInt(20) == 0) level.setBlockAt(xx, yy, zz, Block.SHROOMLIGHT);
                        else level.setBlockAt(xx, yy, zz, this.getLeafBlock());
                    }
                }
            }

            for (int zz = z - mid; zz <= z + mid; zz++) {
                int zOff = Math.abs(zz - z);
                for (int xx = x - mid; xx <= x + mid; xx+=mid*2) {
                    int xOff = Math.abs(xx - x);
                    if (xOff == mid && zOff == mid && (random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {
                        if(random.nextBoundedInt(20) == 0) level.setBlockAt(xx, yy, zz, Block.SHROOMLIGHT);
                        else level.setBlockAt(xx, yy, zz, this.getLeafBlock());
                    }
                }
            }
        }

        for (int yy = y - 4 + treeHeight; yy <= y + this.treeHeight-3; ++yy) {
            for (int xx = x - mid; xx <= x + mid; xx++) {
                for (int zz = z - mid; zz <= z + mid; zz += mid*2) {
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {
                        if(random.nextBoundedInt(3) == 0) {
                            for(int i = 0; i < random.nextBoundedInt(5); i++) {
                                if (!Block.solid[level.getBlockIdAt(xx, yy-i, zz)]) level.setBlockAt(xx, yy-i, zz, getLeafBlock());
                            }
                        }
                    }
                }
            }

            for (int zz = z - mid; zz <= z + mid; zz++) {
                for (int xx = x - mid; xx <= x + mid; xx+=mid*2) {
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {
                        if(random.nextBoundedInt(3) == 0) {
                            for(int i = 0; i < random.nextBoundedInt(4); i++) {
                                if (!Block.solid[level.getBlockIdAt(xx, yy-i, zz)]) level.setBlockAt(xx, yy-i, zz, getLeafBlock());
                            }
                        }
                    }
                }
            }
        }

        for(int xCanopy = x-mid+1; xCanopy <= x+mid-1; xCanopy++) {
            for(int zCanopy = z-mid+1; zCanopy <= z+mid-1; zCanopy++) {
                if (!Block.solid[level.getBlockIdAt(xCanopy, y+treeHeight, zCanopy)]) level.setBlockAt(xCanopy, y+treeHeight, zCanopy, getLeafBlock());
            }
        }
    }

    @Override
    protected void placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        level.setBlockAt(x, y, z, getTrunkBlock());
        for (int yy = 0; yy < trunkHeight; ++yy) {
            int blockId = level.getBlockIdAt(x, y + yy, z);
            if (this.overridable(blockId)) {
                level.setBlockAt(x, y + yy, z, this.getTrunkBlock());
            }
        }
    }
}