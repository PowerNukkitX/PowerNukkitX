package cn.nukkit.inventory;

import cn.nukkit.Player;

public class BigCraftingGridInventory extends CraftingGridInventory {
    public BigCraftingGridInventory(Player player) {
        super(player, InventoryType.INVENTORY, 9);
    }
}
