package cn.nukkit.inventory;

import cn.nukkit.Player;

public abstract class CraftingGridInventory extends BaseInventory {
    public CraftingGridInventory(Player player, InventoryType type, int size) {
        super(player, type, size);
    }

    @Override
    public Player getHolder() {
        return (Player) super.getHolder();
    }
}
