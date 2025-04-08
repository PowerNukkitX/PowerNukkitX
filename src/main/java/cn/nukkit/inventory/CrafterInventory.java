package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.recipe.Input;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Map;

public class CrafterInventory extends ContainerInventory implements CraftTypeInventory, InputInventory {

    private int disabledSlots = 0;

    public CrafterInventory(BlockEntityCrafter crafter) {
        super(crafter, InventoryType.CRAFTER, 9);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.CRAFTER_BLOCK_CONTAINER);
        }
    }

    @Override
    public BlockEntityCrafter getHolder() {
        return (BlockEntityCrafter) super.getHolder();
    }

    @Override
    public Input getInput() {
        Item[] item1 = List.of(getItem(0), getItem(1), getItem(2)).toArray(Item.EMPTY_ARRAY);
        Item[] item2 = List.of(getItem(3), getItem(4), getItem(5)).toArray(Item.EMPTY_ARRAY);
        Item[] item3 = List.of(getItem(6), getItem(7), getItem(8)).toArray(Item.EMPTY_ARRAY);
        Item[][] items = new Item[][]{item1, item2, item3};
        return new Input(3, 3, items);
    }


    public int getLockedBitMask() {
        return disabledSlots;
    }

    public void setLockedBitMask(@Range(from = 0, to = 0b111111111) int mask) {
        disabledSlots = mask;
    }

    public boolean isLocked(@Range(from = 0, to = 8) int slot) {
        return  ((disabledSlots & (1L << slot)) != 0);
    }

    public void setSlotState(@Range(from = 0, to = 8) int slot, boolean state) {
        this.setLockedBitMask(state ? disabledSlots ^ (1 << slot) : disabledSlots | (1 << slot));
    }
}
