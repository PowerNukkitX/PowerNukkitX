package cn.nukkit.inventory;

import cn.nukkit.entity.item.EntityChestMinecart;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;

public class MinecartChestInventory extends ContainerInventory {

    public MinecartChestInventory(EntityChestMinecart minecart) {
        super(minecart, InventoryType.MINECART_CHEST, 27);
    }

    @Override
    public EntityChestMinecart getHolder() {
        return (EntityChestMinecart) this.holder;
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
    public boolean canCauseVibration() {
        return true;
    }
}
