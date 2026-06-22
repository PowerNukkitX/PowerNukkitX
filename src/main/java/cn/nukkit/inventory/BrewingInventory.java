package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.jetbrains.annotations.Range;

import java.util.Map;

public class BrewingInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    public BrewingInventory(BlockEntityBrewingStand brewingStand) {
        super(brewingStand, ContainerType.BREWING_STAND, 5);//1 INPUT, 3 POTION, 1 fuel
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        map.put(0, ContainerEnumName.BREWING_STAND_INPUT_CONTAINER);
        map.put(1, ContainerEnumName.BREWING_STAND_RESULT_CONTAINER);
        map.put(2, ContainerEnumName.BREWING_STAND_RESULT_CONTAINER);
        map.put(3, ContainerEnumName.BREWING_STAND_RESULT_CONTAINER);
        map.put(4, ContainerEnumName.BREWING_STAND_FUEL_CONTAINER);
    }

    @Override
    public BlockEntityBrewingStand getHolder() {
        return (BlockEntityBrewingStand) this.holder;
    }

    public Item getIngredient() {
        return getItem(0);
    }

    public void setIngredient(Item item) {
        setItem(0, item);
    }

    public void setResult(@Range(from = 1, to = 3) int index, Item result) {
        setItem(index, result);
    }

    public Item getResult(@Range(from = 1, to = 3) int index) {
        return getItem(index);
    }

    public void setFuel(Item fuel) {
        setItem(4, fuel);
    }

    public Item getFuel() {
        return getItem(4);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        if (index >= 1 && index <= 3) {
            this.getHolder().updateBlock();
        }

        this.getHolder().scheduleUpdate();
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}