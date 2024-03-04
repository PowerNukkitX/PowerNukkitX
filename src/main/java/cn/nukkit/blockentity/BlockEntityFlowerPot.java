package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Snake1999
 * @since 2016/2/4
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {
    public BlockEntityFlowerPot(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public boolean isBlockEntityValid() {
        String blockId = getBlock().getId();
        return blockId == Block.FLOWER_POT;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable());
        if (namedTag.containsCompound("PlantBlock"))
            tag.putCompound("PlantBlock", namedTag.getCompound("PlantBlock"));
        return tag;
    }
}
