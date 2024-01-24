package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockMangrovePropagule;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSource;

public class ObjectMangroveTree extends TreeGenerator {

    private static final Block LOG = Block.get(BlockID.MANGROVE_LOG);
    private static final Block ROOTS = Block.get(BlockID.MANGROVE_ROOTS);

    @Override
    public boolean generate(BlockManager level, RandomSource rand, Vector3 position) {
        int i = rand.nextInt(3) + 8;
        int j = position.getFloorX();
        int k = position.getFloorY();
        int l = position.getFloorZ();

        int i2 = k + i;

        if (k >= -63 && k + i + 2 < 320) {
            Vector3 blockpos = position.down();
            for (int il = 0; il < i + 1; il++) {
                if (il > 2) {
                    placeLogAt(level, j, il + k, l);
                } else {
                    placeRootAt(level, j + 1, il + k, l);
                    placeRootAt(level, j - 1, il + k, l);
                    placeRootAt(level, j, il + k, l + 1);
                    placeRootAt(level, j, il + k, l - 1);
                }
            }
            placeRootAt(level, j + 2, 0 + k, l);
            placeRootAt(level, j - 2, 0 + k, l);
            placeRootAt(level, j, 0 + k, l + 2);
            placeRootAt(level, j, 0 + k, l - 2);
            for (int i3 = -2; i3 <= 1; ++i3) {
                for (int l3 = -2; l3 <= 1; ++l3) {
                    int k4 = 1;
                    int offsetX = rand.nextInt(0, 1);
                    int offsetY = rand.nextInt(0, 1);
                    int offsetZ = rand.nextInt(0, 1);
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
                    offsetX = rand.nextInt(-1, 0);
                    offsetY = rand.nextInt(-1, 0);
                    offsetZ = rand.nextInt(-1, 0);

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

    private void placeLogAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR || material == Block.MANGROVE_LEAVES || material == Block.MANGROVE_PROPAGULE) {
            worldIn.setBlockAt(blockpos, LOG);
        }
    }

    private void placeRootAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR || material == Block.MANGROVE_LEAVES || material == Block.MANGROVE_PROPAGULE) {
            worldIn.setBlockAt(blockpos, ROOTS);
        }
    }

    private void placeLeafAt(BlockManager worldIn, int x, int y, int z, RandomSource random) {
        Vector3 blockpos = new Vector3(x, y, z);
        Block material = worldIn.getBlockAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material.isAir() || (material instanceof BlockMangrovePropagule propagule && propagule.isHanging())) {
            worldIn.setBlockAt(blockpos, Block.get(BlockID.MANGROVE_LEAVES));
            if (random.nextInt(7) == 0) {
                placePropaguleAt(worldIn, blockpos.getFloorX(), blockpos.getFloorY() - 1, blockpos.getFloorZ());
            }
        }
    }

    private void placePropaguleAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());
        if (material == Block.AIR) {
            //Todo: Fix hanging mangrove propagule
        }
    }
}
