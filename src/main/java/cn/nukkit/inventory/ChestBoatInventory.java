package cn.nukkit.inventory;

import cn.nukkit.entity.item.EntityChestBoat;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;


public class ChestBoatInventory extends ContainerInventory {
    public ChestBoatInventory(EntityChestBoat holder) {
        super(holder, InventoryType.CHEST_BOAT, 27);
    }

    @Override
    public EntityChestBoat getHolder() {
        return (EntityChestBoat) super.getHolder();
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
}
