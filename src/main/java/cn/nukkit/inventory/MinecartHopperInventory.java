package cn.nukkit.inventory;


import cn.nukkit.entity.item.EntityHopperMinecart;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;

import java.util.Map;

public class MinecartHopperInventory extends ContainerInventory {

    public MinecartHopperInventory(EntityHopperMinecart minecart) {
        super(minecart, InventoryType.MINECART_HOPPER, 5);
    }

    @Override
    public EntityHopperMinecart getHolder() {
        return (EntityHopperMinecart) super.getHolder();
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
