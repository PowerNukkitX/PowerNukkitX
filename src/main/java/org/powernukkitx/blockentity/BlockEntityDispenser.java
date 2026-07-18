package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.inventory.DispenserInventory;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;


public class BlockEntityDispenser extends BlockEntityEjectable {


    public BlockEntityDispenser(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected DispenserInventory createInventory() {
        inventory = new DispenserInventory(this);
        return getInventory();
    }

    @Override
    protected String getBlockEntityName() {
        return BlockEntity.DISPENSER;
    }

    @Override
    public DispenserInventory getInventory() {
        return (DispenserInventory) inventory;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock().getId() == BlockID.DISPENSER;
    }
}
