package org.powernukkitx.inventory;

import org.powernukkitx.blockentity.BlockEntityNameable;

public interface BlockEntityInventoryNameable extends InventoryNameable{
    BlockEntityNameable getBlockEntityInventoryHolder();

    @Override
    default String getInventoryTitle() {
        return getBlockEntityInventoryHolder().getName();
    }

    @Override
    default void setInventoryTitle(String name) {
        getBlockEntityInventoryHolder().setName(name);
    }
}
