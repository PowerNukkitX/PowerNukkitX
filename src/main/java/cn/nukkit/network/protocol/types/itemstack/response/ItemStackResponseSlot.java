package cn.nukkit.network.protocol.types.itemstack.response;

import lombok.NonNull;
import lombok.Value;

/**
 * ItemEntry holds information on what item stack should be present in a specific slot.
 */
@Value
public class ItemStackResponseSlot {
    int slot;
    int hotbarSlot;
    int count;

    /**
     * stackNetworkID is the network ID of the new stack at a specific slot.
     */
    int stackNetworkId;

    /**
     * Holds the final custom name of a renamed item, if relevant.
     *
     * @since v422
     */
    @NonNull String customName;

    /**
     * @since v428
     */
    int durabilityCorrection;

    public ItemStackResponseSlot(int slot, int hotbarSlot, int count, int stackNetworkId, String customName, int durabilityCorrection) {
        this.slot = slot;
        this.hotbarSlot = hotbarSlot;
        this.count = count;
        this.stackNetworkId = stackNetworkId;
        this.customName = customName;
        this.durabilityCorrection = durabilityCorrection;
    }
}