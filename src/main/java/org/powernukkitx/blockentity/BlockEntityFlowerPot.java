package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

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
        if (nbt.containsCompound("PlantBlock"))
            tag.putCompound("PlantBlock", nbt.getCompound("PlantBlock"));
        return tag;
    }
}