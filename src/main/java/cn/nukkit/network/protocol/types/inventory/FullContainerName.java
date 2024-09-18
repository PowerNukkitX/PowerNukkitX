package cn.nukkit.network.protocol.types.inventory;

import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.Value;

import javax.annotation.Nullable;

@Value
public class FullContainerName {

    ContainerSlotType container;

    @Nullable
    Integer dynamicId;

}
