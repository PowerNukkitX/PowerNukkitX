package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.blockentity.BlockEntityShelf;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;

/**
 * @author Buddelbubi
 * @since 2024/11/07
 */
public class ShelfInventory extends ContainerInventory {

    public ShelfInventory(InventoryHolder holder) {
        super(holder, InventoryType.CONTAINER, 3); //No InventoryType for this one??
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.LEVEL_ENTITY);
        }
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < this.getSize(); i++) {
            map.put(i, ContainerSlotType.INVENTORY);
        }
        return map;
    }

    @Override
    public BlockEntityShelf getHolder() {
        return (BlockEntityShelf) super.getHolder();
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
