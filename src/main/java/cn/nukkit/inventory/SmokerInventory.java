package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityFurnace;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;

public class SmokerInventory extends SmeltingInventory {
    public SmokerInventory(BlockEntityFurnace furnace) {
        super(furnace, ContainerType.SMOKER, 3);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        map.put(0, ContainerEnumName.SMOKER_INGREDIENT_CONTAINER);
        map.put(1, ContainerEnumName.FURNACE_FUEL_CONTAINER);
        map.put(2, ContainerEnumName.FURNACE_RESULT_CONTAINER);
    }
}
