package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockAzaleaLeaves;
import cn.nukkit.block.BlockAzaleaLeavesFlowered;
import cn.nukkit.block.BlockDirtWithRoots;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockOakLog;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectAzaleaTree extends TreeGenerator {

    private static final BlockState OAK_LOG = BlockOakLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y));
    private static final BlockState DIRT_WITH_ROOTS = BlockDirtWithRoots.PROPERTIES.getDefaultState();
    private static final BlockState AZALEA_LEAVES_FLOWERED = BlockAzaleaLeavesFlowered.PROPERTIES.getDefaultState();
    private static final BlockState AZALEA_LEAVES = BlockAzaleaLeaves.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
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
        level.setBlockStateAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), DIRT_WITH_ROOTS);
    }

    private void placeLogAt(BlockManager worldIn, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdIfCachedOrLoaded(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

        if (material.equals(BlockID.AIR) || material.equals(BlockID.AZALEA_LEAVES) || material.equals(BlockID.AZALEA_LEAVES_FLOWERED)) {
            worldIn.setBlockStateAt(blockpos, OAK_LOG);
        }
    }

    private void placeLeafAt(BlockManager worldIn, int x, int y, int z, RandomSourceProvider random) {
        Vector3 blockpos = new Vector3(x, y, z);
        String material = worldIn.getBlockIdIfCachedOrLoaded(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

        if (material.equals(BlockID.AIR)) {
            if (random.nextInt(3) == 1) {
                worldIn.setBlockStateAt(blockpos, AZALEA_LEAVES_FLOWERED);
            } else {
                worldIn.setBlockStateAt(blockpos, AZALEA_LEAVES);
            }
        }
    }
}