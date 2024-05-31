package cn.nukkit.inventory;


import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.item.Item;


public class CampfireInventory extends ContainerInventory {
    /**
     * @deprecated 
     */
    
    public CampfireInventory(BlockEntityCampfire campfire) {
        super(campfire, InventoryType.NONE, 4);
    }

    @Override
    public BlockEntityCampfire getHolder() {
        return (BlockEntityCampfire) this.holder;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
        this.getHolder().scheduleUpdate();
        this.getHolder().spawnToAll();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean open(Player who) {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close(Player who) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onOpen(Player who) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
    }
}
