package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityHopper;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

/**
 * @author CreeperFace
 * @since 8.5.2017
 */
public class HopperInventory extends ContainerInventory {

    public HopperInventory(BlockEntityHopper hopper) {
        super(hopper, ContainerType.HOPPER, 5);
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
    public BlockEntityHopper getHolder() {
        return (BlockEntityHopper) super.getHolder();
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
