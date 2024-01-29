package cn.nukkit.inventory;

import cn.nukkit.block.BlockCraftingTable;
import cn.nukkit.item.Item;
import cn.nukkit.recipe.Input;

import java.util.List;

public class CraftingTableInventory extends ContainerInventory implements CraftTypeInventory, InputInventory {
    public CraftingTableInventory(BlockCraftingTable table) {
        super(table, InventoryType.INVENTORY, 9);
    }

    @Override
    public BlockCraftingTable getHolder() {
        return (BlockCraftingTable) super.getHolder();
    }

    @Override
    public Input getInput() {
        Item[] item1 = List.of(getItem(0), getItem(1), getItem(2)).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(getItem(3), getItem(4), getItem(5)).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(getItem(6), getItem(7), getItem(8)).toArray(Item.EMPTY_ARRAY);
        Item[][] items = new Item[][]{item1, item2, item3};
        return new Input(3, 3, items);
    }
}
