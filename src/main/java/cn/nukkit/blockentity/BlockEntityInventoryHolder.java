package cn.nukkit.blockentity;

import cn.nukkit.inventory.InventoryHolder;

/**
 * Semantic interface
 */
public interface BlockEntityInventoryHolder extends BlockEntityNameable, InventoryHolder {
    default 
    /**
     * @deprecated 
     */
    String getInventoryTitle() {
        return getName();
    }

    default 
    /**
     * @deprecated 
     */
    void setInventoryTitle(String name) {
        setName(name);
    }
}
