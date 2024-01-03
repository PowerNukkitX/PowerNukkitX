package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSource;

public class ObjectAzaleaTree extends TreeGenerator {

    private static final Block OAK_LOG = Block.get(BlockID.OAK_LOG);

    @Override
    public boolean generate(BlockManager level, RandomSource rand, Vector3 position) {
        int i = rand.nextInt(2) + 2;
        int j = position.getFloorX();
        int k = position.getFloorY();
        int l = position.getFloorZ();

        int i2 = k + i;

        if (k >= -63 && k + i + 2 < 320) {
            Vector3 blockPosition = position.down();
            for (int il = 0; il < i + 1; il++) {
                placeLogAt(level, j, il + k, l);
            }
            this.setDirtAt(level, blockPosition);


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

    @Override
    protected void setDirtAt(BlockManager level, Vector3 pos) {
        if (!level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z).equals(BlockID.DIRT_WITH_ROOTS)) {
            level.setBlockAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), Block.get(BlockID.DIRT_WITH_ROOTS));
        }
    }

    private void placeLogAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

        if (material.equals(BlockID.AIR) || material.equals(BlockID.AZALEA_LEAVES) || material.equals(BlockID.AZALEA_LEAVES_FLOWERED)) {
            worldIn.setBlockAt(blockpos, OAK_LOG);
        }
    }

    private void placeLeafAt(BlockManager worldIn, int x, int y, int z, RandomSource random) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

        if (material.equals(BlockID.AIR)) {
            if (random.nextInt(3) == 1) {
                worldIn.setBlockAt(blockpos, Block.get(BlockID.AZALEA_LEAVES_FLOWERED));
            } else {
                worldIn.setBlockAt(blockpos, Block.get(BlockID.AZALEA_LEAVES));
            }
        }
    }
}