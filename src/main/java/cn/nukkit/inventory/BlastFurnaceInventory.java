package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityFurnace;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

public class BlastFurnaceInventory extends SmeltingInventory {
    public BlastFurnaceInventory(BlockEntityFurnace furnace) {
        super(furnace, ContainerType.BLAST_FURNACE, 3);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        map.put(0, ContainerEnumName.BLAST_FURNACE_INGREDIENT_CONTAINER);
        map.put(1, ContainerEnumName.FURNACE_FUEL_CONTAINER);
        map.put(2, ContainerEnumName.FURNACE_RESULT_CONTAINER);
    }
}
