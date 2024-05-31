package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.DispenserInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;


public class BlockEntityDispenser extends BlockEntityEjectable {
    /**
     * @deprecated 
     */
    


    public BlockEntityDispenser(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected DispenserInventory createInventory() {
        inventory = new DispenserInventory(this);
        return getInventory();
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getBlockEntityName() {
        return BlockEntity.DISPENSER;
    }

    @Override
    public DispenserInventory getInventory() {
        return (DispenserInventory) inventory;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getLevelBlock().getId() == BlockID.DISPENSER;
    }
}
