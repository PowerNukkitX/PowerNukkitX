package cn.nukkit.network.protocol.types.inventory;

import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.Value;

@Value
public class FullContainerName {

    ContainerSlotType container;
    int dynamicId;

}
