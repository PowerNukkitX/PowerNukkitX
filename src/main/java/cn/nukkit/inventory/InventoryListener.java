package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public interface InventoryListener {

    @PowerNukkitOnly
    void onInventoryChanged(Inventory inventory, Item oldItem, int slot);
}
