package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.recipe.Input;

import java.util.List;

public class CraftingGridInventory extends BaseInventory implements InputInventory {
    public CraftingGridInventory(Player holder) {
        super(holder, InventoryType.INVENTORY, 4);
    }

    @Override
    public Player getHolder() {
        return (Player) super.getHolder();
    }

    @Override
    public Input getInput() {
        Item[] item1 = List.of(getItem(0), getItem(1)).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(getItem(2), getItem(3)).toArray(Item.EMPTY_ARRAY);
        Item[][] items = new Item[][]{item1, item2};
        return new Input(2, 2, items);
    }
}
