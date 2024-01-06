package cn.nukkit.network.protocol.types.itemstack.response;

import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.Value;

import java.util.List;

/**
 * ContainerEntry holds information on what slots in a container have what item stack in them.
 */
@Value
public class ItemStackResponseContainer {
    /**
     * container that the slots that follow are in.
     */
    ContainerSlotType container;

    /**
     * items holds information on what item stack should be present in specific slots in the container.
     */
    List<ItemStackResponseSlot> items;
}
