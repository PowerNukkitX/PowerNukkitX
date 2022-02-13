package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.Item;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class ObjectAzaleaTree extends TreeGenerator {

    @Override
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        int i = rand.nextBoundedInt(2) + 2;
        int j = position.getFloorX();
        int k = position.getFloorY();
        int l = position.getFloorZ();

        int i2 = k + i;

        if (k >= -63 && k + i + 1 < 320) {
            Vector3 trackLocation = position.clone();
            Vector3 blockpos = position.down();
            for (int il = 0; il < i + 1; il++) {
                placeLogAt(level, trackLocation);
                trackLocation.up();
            }
            this.setDirtAt(level, blockpos);


            for (int i3 = -2; i3 <= 1; ++i3) {
                for (int l3 = -2; l3 <= 1; ++l3) {
                    int k4 = 0;
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

    @Override
    protected void setDirtAt(ChunkManager level, Vector3 pos) {
        if (level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != Item.DIRT_WITH_ROOTS) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(BlockID.DIRT_WITH_ROOTS));
        }
    }

    private void placeLogAt(ChunkManager worldIn, Vector3 pos) {
        int material = worldIn.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());

        if (material == Block.AIR || material == Block.AZALEA_LEAVES || material == Block.AZALEA_LEAVES_FLOWERED) {
            this.setBlockAndNotifyAdequately(worldIn, pos, Block.get(BlockID.LOG));
        }
    }

    private void placeLeafAt(ChunkManager worldIn, int x, int y, int z, NukkitRandom random) {
        Vector3 blockpos = new Vector3(x, y, z);
        int material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

        if (material == Block.AIR) {
            if (random.nextBoundedInt(3) == 1) {
                this.setBlockAndNotifyAdequately(worldIn, blockpos, Block.get(BlockID.AZALEA_LEAVES_FLOWERED));
            } else {
                this.setBlockAndNotifyAdequately(worldIn, blockpos, Block.get(BlockID.AZALEA_LEAVES));
            }
        }
    }
}
