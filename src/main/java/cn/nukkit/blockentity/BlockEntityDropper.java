package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.DropperInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;


public class BlockEntityDropper extends BlockEntityEjectable {
    /**
     * @deprecated 
     */
    


    public BlockEntityDropper(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected DropperInventory createInventory() {
        inventory = new DropperInventory(this);
        return getInventory();
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getBlockEntityName() {
        return BlockEntity.DROPPER;
    }

    @Override
    public DropperInventory getInventory() {
        return (DropperInventory) inventory;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getLevelBlock().getId() == BlockID.DROPPER;
    }
}
