package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

import java.util.Map;

public class BlastFurnaceInventory extends SmeltingInventory {
    public BlastFurnaceInventory(BlockEntityFurnace furnace) {
        super(furnace, InventoryType.BLAST_FURNACE, 3);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        map.put(0, ContainerSlotType.BLAST_FURNACE_INGREDIENT);
        map.put(1, ContainerSlotType.FURNACE_FUEL);
        map.put(2, ContainerSlotType.FURNACE_RESULT);
    }
}
