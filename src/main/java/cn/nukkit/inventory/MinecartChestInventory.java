package cn.nukkit.inventory;

import cn.nukkit.entity.item.EntityChestMinecart;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

public class MinecartChestInventory extends ContainerInventory {

    public MinecartChestInventory(EntityChestMinecart minecart) {
        super(minecart, ContainerType.MINECART_CHEST, 27);
    }

    @Override
    public EntityChestMinecart getHolder() {
        return (EntityChestMinecart) this.holder;
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

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
