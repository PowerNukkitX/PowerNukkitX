package cn.nukkit.inventory;

import cn.nukkit.entity.item.EntityChestBoat;


public class ChestBoatInventory extends ContainerInventory {
    public ChestBoatInventory(EntityChestBoat holder) {
        super(holder, InventoryType.CHEST_BOAT, 27);
    }

    @Override
    public EntityChestBoat getHolder() {
        return (EntityChestBoat) super.getHolder();
    }
}
