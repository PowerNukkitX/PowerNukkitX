package cn.nukkit.inventory;

import cn.nukkit.block.BlockCraftingTable;

public class CraftingTableInventory extends ContainerInventory implements CraftTypeInventory {
    public CraftingTableInventory(BlockCraftingTable table) {
        super(table, InventoryType.INVENTORY, 9);
    }

    @Override
    public BlockCraftingTable getHolder() {
        return (BlockCraftingTable) super.getHolder();
    }
}
