package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


public abstract class EjectableInventory extends ContainerInventory {


    public EjectableInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }
}
