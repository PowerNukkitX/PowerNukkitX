package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

public class BlockEntityEnderChest extends BlockEntitySpawnable implements BlockEntityNameable {

    public BlockEntityEnderChest(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId().equals(Block.ENDER_CHEST);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag spawnCompound = super.getSpawnCompound();
        spawnCompound.putBoolean("isMovable", this.isMovable());
        if (this.hasName()) {
            spawnCompound.put("CustomName", this.nbt.get("CustomName"));
        }
        return spawnCompound;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "EnderChest";
    }

    @Override
    public boolean hasName() {
        return this.nbt.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.nbt.remove("CustomName");
            return;
        }

        this.nbt.putString("CustomName", name);
    }
}
