package cn.nukkit.inventory;



import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class FurnaceTypeInventory extends SmeltingInventory {
    public FurnaceTypeInventory(BlockEntityFurnace furnace) {
        super(furnace, InventoryType.FURNACE, 3);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        map.put(0, ContainerSlotType.FURNACE_INGREDIENT);
        map.put(1, ContainerSlotType.FURNACE_FUEL);
        map.put(2, ContainerSlotType.FURNACE_RESULT);
    }
}
