package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.item.EntityChestBoat;


public class ChestBoatInventory extends ContainerInventory{

    public ChestBoatInventory(EntityChestBoat holder) {
        super(holder, InventoryType.CHEST_BOAT);
    }

    @Override
    public EntityChestBoat getHolder() {
        return (EntityChestBoat) super.getHolder();
    }
}
