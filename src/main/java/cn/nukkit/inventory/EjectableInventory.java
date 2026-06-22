package cn.nukkit.inventory;


import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

public abstract class EjectableInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    public EjectableInventory(InventoryHolder holder, ContainerType type, int size) {
        super(holder, type, size);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.LEVEL_ENTITY_CONTAINER);
        }
    }
}
