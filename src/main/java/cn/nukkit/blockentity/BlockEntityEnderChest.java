package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

public class BlockEntityEnderChest extends BlockEntitySpawnable implements BlockEntityNameable {

    public BlockEntityEnderChest(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId().equals(Block.ENDER_CHEST);
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder spawnCompound = super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", this.isMovable());
        if (this.hasName()) {
            spawnCompound.put("CustomName", this.namedTag.get("CustomName"));
        }
        return spawnCompound.build();
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "EnderChest";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag = this.namedTag.toBuilder().putString("CustomName", name).build();
    }
}
