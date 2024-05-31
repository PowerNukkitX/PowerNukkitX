package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;


public class BlockEntityBarrel extends BlockEntitySpawnableContainer{
    /**
     * @deprecated 
     */
    


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
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.BARREL;
    }

    @Override
    public BarrelInventory getInventory() {
        return (BarrelInventory) inventory;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Barrel";
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
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }
}
