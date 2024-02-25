package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

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
        CompoundTag spawnCompound = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable());
        if (this.hasName()) {
            spawnCompound.put("CustomName", this.namedTag.get("CustomName"));
        }
        return spawnCompound;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "EnderChest";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }
}
