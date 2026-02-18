package cn.nukkit.inventory;


import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;

import java.util.Map;

public abstract class EjectableInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    public EjectableInventory(InventoryHolder holder, InventoryType type, int size) {
        super(holder, type, size);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerSlotType.LEVEL_ENTITY);
        }
    }
}
