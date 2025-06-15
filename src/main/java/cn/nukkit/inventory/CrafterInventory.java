package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.block.BlockCrafter;
import cn.nukkit.blockentity.BlockEntityCrafter;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.recipe.Input;
import cn.nukkit.recipe.Recipe;
import cn.nukkit.recipe.ShapedRecipe;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public Item[] addItem(Item... slots) {

        List<Item> itemSlots = new ArrayList<>();

        Map<Integer, Item> inventory = new HashMap<>();
        for(int i = 0; i < getSize(); i++) inventory.put(i, getUnclonedItem(i));
        for(Item toAdd : slots) {
            Item clone = toAdd.clone();
            clone.setCount(1);
            Optional<Map.Entry<Integer, Item>> optional = inventory.entrySet().stream().filter(entry -> canAddItem(clone, entry.getKey()) != 0).min(Comparator.comparingInt(o -> o.getValue().getCount()));
            if(optional.isPresent()) {
                int slot = optional.get().getKey();
                if(getItem(slot).isNull()) {
                    setItem(slot, clone);
                } else {
                    toAdd.count--;
                    getUnclonedItem(slot).count++;
                    sendSlot(slot, getViewers());
                }
            } else {
                itemSlots.add(toAdd);
            }
        }
        return itemSlots.toArray(Item.EMPTY_ARRAY);
    }


    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if(super.setItem(index, item, send)) {
            ((BlockCrafter) getHolder().getLevelBlock()).updateAllAroundRedstone();
            return true;
        } else return false;
    }

    @Override
    public boolean clear(int index, boolean send) {
        if(super.clear(index, send)) {
            ((BlockCrafter) getHolder().getLevelBlock()).updateAllAroundRedstone();
            return true;
        }
        return false;
    }

    protected int canAddItem(Item item, int slot) {
        Item current = getItem(slot);
        if(isLocked(slot)) return 0;
        if(current.isNull()) return Math.min(item.getCount(), item.getMaxStackSize());
        if(!current.equals(item)) return 0;
        return Math.min(item.getCount(), item.getMaxStackSize() - getItem(slot).getCount());
    }

    @Override
    public boolean canAddItem(Item item) {
        item = item.clone();
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        for (int i = 0; i < this.getSize(); ++i) {
            if(isLocked(i)) continue;
            Item slot = this.getUnclonedItem(i);
            if (item.equals(slot, checkDamage, checkTag)) {
                int diff;
                if ((diff = Math.min(slot.getMaxStackSize(), this.getMaxStackSize()) - slot.getCount()) > 0) {
                    item.setCount(item.getCount() - diff);
                }
            } else if (slot.isNull()) {
                item.setCount(item.getCount() - Math.min(slot.getMaxStackSize(), this.getMaxStackSize()));
            }

            if (item.getCount() <= 0) {
                return true;
            }
        }
        return false;
    }

    public Recipe getRecipe() {
        Input input = getInput();
        ShapedRecipe.tryShrinkMatrix(input);
        Recipe recipe = Registries.RECIPE.findShapedRecipe(input);
        if(recipe != null) return recipe;
        return Registries.RECIPE.findShapelessRecipe(input.getFlatItems());
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
        Server.broadcastPacket(getViewers(), getHolder().getSpawnPacket());
        ((BlockCrafter) getHolder().getLevelBlock()).updateAllAroundRedstone();
    }

    public int getComperatorOutput() {
        int output = 0;
        for(int i = 0; i < getSize(); i++) {
            if(!getItem(i).isNull() || isLocked(i)) output++;
        }
        return output;
    }
}
