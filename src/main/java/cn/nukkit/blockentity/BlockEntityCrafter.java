package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.CrafterInventory;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;

public class BlockEntityCrafter extends BlockEntitySpawnableContainer {

    public BlockEntityCrafter(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    protected CrafterInventory requireContainerInventory() {
        return new CrafterInventory(this);
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putInt("crafting_ticks_remaining", 0)
                .putShort("disabled_slots", (short) getInventory().getLockedBitMask())
                .build();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.containsKey("disabledSlots")) {
            this.nbt.putShort("disabledSlots", (short) 0);
        }
        this.getInventory().setLockedBitMask(this.getNbt().getShort("disabledSlots"));
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putShort("disabledSlots", (short) getInventory().getLockedBitMask());
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
        return this.hasName() ? this.getNbt().getString("CustomName") : "Crafter";
    }

    @Override
    public boolean hasName() {
        return this.nbt.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }
}
