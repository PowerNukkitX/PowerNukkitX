package cn.nukkit.inventory;

import cn.nukkit.Player;

public class CraftingGridInventory extends BaseInventory {
    public CraftingGridInventory(Player holder) {
        super(holder, InventoryType.INVENTORY, 4);
    }

    @Override
    public Player getHolder() {
        return (Player) super.getHolder();
    }
}
