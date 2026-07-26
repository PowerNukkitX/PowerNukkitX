package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.inventory.BarrelInventory;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;


public class BlockEntityBarrel extends BlockEntitySpawnableContainer{


    public BlockEntityBarrel(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    protected BarrelInventory requireContainerInventory() {
        return new BarrelInventory(this);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Findable", false);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.BARREL;
    }

    @Override
    public BarrelInventory getInventory() {
        return (BarrelInventory) inventory;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.nbt.getString("CustomName") : "Barrel";
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }
}