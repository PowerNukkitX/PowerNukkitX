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
        int i = rand.nextBoundedInt(3) + rand.nextBoundedInt(2) + 6;

        int j = position.getFloorX();
        int k = position.getFloorY();
        int l = position.getFloorZ();

        int k1 = j;
        int l1 = l;
        int i2 = k + i;

        if (k >= -63 && k + i + 1 < 320) {
            return false;
        }
        Vector3 trackLocation = position.clone();
        Vector3 blockpos = position.down();
        this.setDirtAt(level, blockpos);
        for (int il = 0; il < 2 + rand.nextBoundedInt(3); il++) {
            placeLogAt(level, trackLocation);
            trackLocation.up();
        }
        Vector3 leafLocation = trackLocation.clone().up();


        for (int i3 = -1; i3 <= 0; ++i3) {
            for (int l3 = -1; l3 <= 0; ++l3) {

                int k4 = -1;
                this.placeLeafAt(level, k1 + i3, i2 + k4, l1 + l3, rand);
                this.placeLeafAt(level, k1 - i3, i2 + k4, l1 + l3, rand);
                this.placeLeafAt(level, k1 + i3, i2 + k4, l1 - l3, rand);
                this.placeLeafAt(level, k1 - i3, i2 + k4, l1 - l3, rand);


                k4 = 1;
                this.placeLeafAt(level, k1 + i3, i2 + k4, l1 + l3, rand);
                this.placeLeafAt(level, k1 - i3, i2 + k4, l1 + l3, rand);
                this.placeLeafAt(level, k1 + i3, i2 + k4, l1 - l3, rand);
                this.placeLeafAt(level, k1 - i3, i2 + k4, l1 - l3, rand);

                k4 = 2;
                int offsetX = rand.nextRange(-1, 1);
                int offsetY = rand.nextRange(-1, 1);
                int offsetZ = rand.nextRange(-1, 1);

                this.placeLeafAt(level, k1 + i3 + offsetX, i2 + k4 + offsetY, l1 + l3 + offsetZ, rand);
                this.placeLeafAt(level, k1 - i3 + offsetX, i2 + k4 + offsetY, l1 + l3+ offsetZ, rand);
                this.placeLeafAt(level, k1 + i3 + offsetX, i2 + k4 + offsetY, l1 - l3 + offsetZ, rand);
                this.placeLeafAt(level, k1 - i3 + offsetX, i2 + k4 + offsetY, l1 - l3 + offsetZ, rand);

            }
        }

        return true;
    }

    @Override
    protected void setDirtAt(ChunkManager level, Vector3 pos) {
        if (level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != Item.DIRT_WITH_ROOTS) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(BlockID.DIRT_WITH_ROOTS));
        }
    }

    private void placeLogAt(ChunkManager worldIn, Vector3 pos) {
        if (this.canGrowInto(worldIn.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()))) {
            this.setBlockAndNotifyAdequately(worldIn, pos, Block.get(BlockID.LOG));
        }
    }

    private void placeLeafAt(ChunkManager worldIn, int x, int y, int z, NukkitRandom random) {
        Vector3 blockpos = new Vector3(x, y, z);
        int material = worldIn.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

        if (material == Block.AIR) {
            if (random.nextBoundedInt(4) == 1) {
                this.setBlockAndNotifyAdequately(worldIn, blockpos, Block.get(BlockID.AZALEA_LEAVES_FLOWERED));
            }
            this.setBlockAndNotifyAdequately(worldIn, blockpos, Block.get(BlockID.AZALEA_LEAVES));

        }
    }
}
