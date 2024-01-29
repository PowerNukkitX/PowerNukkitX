package cn.nukkit.inventory;

import cn.nukkit.block.BlockCraftingTable;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.recipe.Input;
import com.google.common.collect.BiMap;

import java.util.List;
import java.util.Map;

public class CraftingTableInventory extends ContainerInventory implements CraftTypeInventory, InputInventory {
    public CraftingTableInventory(BlockCraftingTable table) {
        super(table, InventoryType.INVENTORY, 9);
    }

    @Override
    public BiMap<Integer, Integer> networkSlotMap() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, 32 + i);
        }
        return map;
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(0, ContainerSlotType.CRAFTING_INPUT);
        }
        return map;
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
