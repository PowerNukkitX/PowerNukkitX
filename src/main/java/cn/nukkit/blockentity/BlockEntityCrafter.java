package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.CrafterInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCrafter extends BlockEntitySpawnableContainer {

    public BlockEntityCrafter(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected CrafterInventory requireContainerInventory() {
        return new CrafterInventory(this);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putInt("crafting_ticks_remaining", 0)
                .putShort("disabled_slots", getInventory().getLockedBitMask());
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.namedTag.contains("disabledSlots")) {
            this.namedTag.putShort("disabledSlots", 0);
        }
        this.getInventory().setLockedBitMask(this.namedTag.getShort("disabledSlots"));
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putShort("disabledSlots", getInventory().getLockedBitMask());
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId().equals(BlockID.CRAFTER);
    }

    @Override
    public CrafterInventory getInventory() {
        return (CrafterInventory) inventory;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Crafter";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }
}
