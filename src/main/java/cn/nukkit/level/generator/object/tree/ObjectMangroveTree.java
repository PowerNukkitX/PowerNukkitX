package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.*;
import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class ObjectMangroveTree extends TreeGenerator {

    private static final Block LOG = Block.get(BlockID.MANGROVE_LOG);
    private static final Block ROOTS = Block.get(BlockID.MANGROVE_ROOTS);

    @Override
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        int i = rand.nextBoundedInt(3) + 8;
        int j = position.getFloorX();
        int k = position.getFloorY();
        int l = position.getFloorZ();

        int i2 = k + i;

        if (k >= -63 && k + i + 2 < 320) {
            Vector3 blockpos = position.down();
            for (int il = 0; il < i + 1; il++) {
                if(il > 2) {
                    placeLogAt(level, j, il + k, l);
                } else {
                    placeRootAt(level, j+1, il + k, l);
                    placeRootAt(level, j-1, il + k, l);
                    placeRootAt(level, j, il + k, l+1);
                    placeRootAt(level, j, il + k, l-1);
                }
            }
            placeRootAt(level, j+2, 0 + k, l);
            placeRootAt(level, j-2, 0 + k, l);
            placeRootAt(level, j, 0 + k, l+2);
            placeRootAt(level, j, 0 + k, l-2);
            for (int i3 = -2; i3 <= 1; ++i3) {
                for (int l3 = -2; l3 <= 1; ++l3) {
                    int k4 = 1;
                    int offsetX = rand.nextRange(0, 1);
                    int offsetY = rand.nextRange(0, 1);
                    int offsetZ = rand.nextRange(0, 1);
                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);

                    k4 = 0;
                    this.placeLeafAt(level, j + i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j + i3, i2 + k4, l - l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l - l3, rand);

                    k4 = 1;
                    this.placeLeafAt(level, j + i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l + l3, rand);
                    this.placeLeafAt(level, j + i3, i2 + k4, l - l3, rand);
                    this.placeLeafAt(level, j - i3, i2 + k4, l - l3, rand);

                    k4 = 2;
                    offsetX = rand.nextRange(-1, 0);
                    offsetY = rand.nextRange(-1, 0);
                    offsetZ = rand.nextRange(-1, 0);

                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l + l3 + offsetZ, rand);
                    this.placeLeafAt(level, j + i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);
                    this.placeLeafAt(level, j - i3 + offsetX, i2 + k4 + offsetY, l - l3 + offsetZ, rand);

                }
            }
            return true;
        }

        return false;
    }

    private void placeLogAt(ChunkManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        int material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR || material == Block.MANGROVE_LEAVES || material == Block.MANGROVE_PROPAGULE) {
            this.setBlockAndNotifyAdequately(worldIn, blockpos, LOG);
        }
    }

    private void placeRootAt(ChunkManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        int material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR || material == Block.MANGROVE_LEAVES || material == Block.MANGROVE_PROPAGULE) {
            this.setBlockAndNotifyAdequately(worldIn, blockpos, ROOTS);
        }
    }
    private void placeLeafAt(ChunkManager worldIn, int x, int y, int z, NukkitRandom random) {
        Vector3 blockpos = new Vector3(x, y, z);
        int material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR || material == Block.MANGROVE_PROPAGULE_HANGING) {
            this.setBlockAndNotifyAdequately(worldIn, blockpos, Block.get(BlockID.MANGROVE_LEAVES));
            if(random.nextBoundedInt(7) == 0) {
                placePropaguleAt(worldIn, blockpos.getFloorX(), blockpos.getFloorY()-1, blockpos.getFloorZ());
            }
        }
    }
    private void placePropaguleAt(ChunkManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        int material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR) {
            //Todo: Fix hanging mangrove propagule
        }
    }
}
