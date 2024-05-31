package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntityEnchantTable extends BlockEntitySpawnable implements BlockEntityInventoryHolder {
    protected EnchantInventory inventory;
    /**
     * @deprecated 
     */
    

    public BlockEntityEnchantTable(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.inventory = new EnchantInventory(this);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.ENCHANTING_TABLE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Enchanting Table";
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

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $1 = super.getSpawnCompound()
                .putBoolean("isMovable", false);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

    @Override
    public EnchantInventory getInventory() {
        return inventory;
    }
}
