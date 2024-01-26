package cn.nukkit.inventory;

import cn.nukkit.Player;

public class SmallCraftingGridInventory extends CraftingGridInventory {
    public SmallCraftingGridInventory(Player holder) {
        super(holder, InventoryType.INVENTORY, 4);
    }
}
