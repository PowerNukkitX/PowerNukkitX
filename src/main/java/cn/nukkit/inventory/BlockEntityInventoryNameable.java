package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityNameable;

public interface BlockEntityInventoryNameable extends InventoryNameable{
    BlockEntityNameable getBlockEntityInventoryHolder();

    @Override
    default 
    /**
     * @deprecated 
     */
    String getInventoryTitle() {
        return getBlockEntityInventoryHolder().getName();
    }

    @Override
    default 
    /**
     * @deprecated 
     */
    void setInventoryTitle(String name) {
        getBlockEntityInventoryHolder().setName(name);
    }
}
