package cn.nukkit.inventory;


import cn.nukkit.entity.item.EntityHopperMinecart;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

public class MinecartHopperInventory extends ContainerInventory {

    public MinecartHopperInventory(EntityHopperMinecart minecart) {
        super(minecart, ContainerType.MINECART_HOPPER, 5);
    }

    @Override
    public EntityHopperMinecart getHolder() {
        return (EntityHopperMinecart) super.getHolder();
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.LEVEL_ENTITY_CONTAINER);
        }
    }

    @Override
    public Map<Integer, ContainerEnumName> slotTypeMap() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < this.getSize(); i++) {
            map.put(i, ContainerEnumName.INVENTORY_CONTAINER);
        }
        return map;
    }
}
