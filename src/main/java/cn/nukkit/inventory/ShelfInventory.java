package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityShelf;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

/**
 * @author Buddelbubi
 * @since 2024/11/07
 */
public class ShelfInventory extends ContainerInventory {

    public ShelfInventory(InventoryHolder holder) {
        super(holder, ContainerType.CONTAINER, 3); //No InventoryType for this one??
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
    public BlockEntityShelf getHolder() {
        return (BlockEntityShelf) super.getHolder();
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
