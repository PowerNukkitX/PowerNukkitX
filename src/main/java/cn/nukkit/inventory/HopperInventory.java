package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
public class HopperInventory extends ContainerInventory {

    public HopperInventory(BlockEntityHopper hopper) {
        super(hopper, InventoryType.HOPPER, 5);
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
    public BlockEntityHopper getHolder() {
        return (BlockEntityHopper) super.getHolder();
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
