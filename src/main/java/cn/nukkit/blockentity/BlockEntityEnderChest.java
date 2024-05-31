package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityEnderChest extends BlockEntitySpawnable implements BlockEntityNameable {
    /**
     * @deprecated 
     */
    

    public BlockEntityEnderChest(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getBlock().getId().equals(Block.ENDER_CHEST);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $1 = super.getSpawnCompound()
                .putBoolean("isMovable", this.isMovable());
        if (this.hasName()) {
            spawnCompound.put("CustomName", this.namedTag.get("CustomName"));
        }
        return spawnCompound;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "EnderChest";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }
}
