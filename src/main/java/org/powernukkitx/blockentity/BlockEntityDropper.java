package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.inventory.DropperInventory;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;


public class BlockEntityDropper extends BlockEntityEjectable {


    public BlockEntityDropper(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected DropperInventory createInventory() {
        inventory = new DropperInventory(this);
        return getInventory();
    }

    @Override
    protected String getBlockEntityName() {
        return BlockEntity.DROPPER;
    }

    @Override
    public DropperInventory getInventory() {
        return (DropperInventory) inventory;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock().getId() == BlockID.DROPPER;
    }
}
