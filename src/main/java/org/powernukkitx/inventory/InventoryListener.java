package org.powernukkitx.inventory;


import org.powernukkitx.item.Item;


public interface InventoryListener {
    void onInventoryChanged(Inventory inventory, Item oldItem, int slot);
}
