package cn.nukkit.blockentity;

import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowerPot;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Snake1999
 * @since 2016/2/4
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {
    public BlockEntityFlowerPot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        //转换旧形式
        if (namedTag.contains("item")) {
            var data = 0;
            if (namedTag.contains("data")) data = namedTag.getInt("data");
            else if (namedTag.contains("mData")) data = namedTag.getInt("mData");
            var block = Block.get(namedTag.getInt("item"), data);
            if (block instanceof BlockFlowerPot.FlowerPotBlock potBlock && potBlock.isPotBlockState()) {
                namedTag.putCompound("PlantBlock", potBlock.getPlantBlockTag());
            }
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.FLOWER_POT_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.FLOWER_POT)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (namedTag.containsCompound("PlantBlock"))
            tag.putCompound("PlantBlock", namedTag.getCompound("PlantBlock"));
        return tag;
    }

}
