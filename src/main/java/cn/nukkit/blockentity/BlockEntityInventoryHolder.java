package cn.nukkit.blockentity;

import cn.nukkit.inventory.InventoryHolder;

/**
 * Semantic interface
 */
public interface BlockEntityInventoryHolder extends BlockEntityNameable, InventoryHolder {
    default String getInventoryTitle() {
        return getName();
    }

    default void setInventoryTitle(String name) {
        setName(name);
    }
}
