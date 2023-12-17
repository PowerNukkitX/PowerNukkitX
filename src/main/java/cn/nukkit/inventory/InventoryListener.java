package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;


public interface InventoryListener {


    void onInventoryChanged(Inventory inventory, Item oldItem, int slot);
}
