package cn.nukkit.network.protocol.types.itemstack.response;

import lombok.NonNull;
import lombok.Value;

/**
 * ItemEntry holds information on what item stack should be present in a specific slot.
 */
@Value
public class ItemStackResponseSlot {
    int requestedSlot;
    int slot;
    int amount;

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
     * @since v766
     */
    String filteredCustomName;

    /**
     * @since v428
     */
    int durabilityCorrection;

    public ItemStackResponseSlot(int requestedSlot, int slot, int amount, int stackNetworkId, String customName, int durabilityCorrection) {
        this(requestedSlot, slot, amount, stackNetworkId, customName, "", durabilityCorrection);
    }

    public ItemStackResponseSlot(int requestedSlot, int slot, int amount, int stackNetworkId, String customName, String filteredCustomName, int durabilityCorrection) {
        this.requestedSlot = requestedSlot;
        this.slot = slot;
        this.amount = amount;
        this.stackNetworkId = stackNetworkId;
        this.customName = customName;
        this.filteredCustomName = filteredCustomName;
        this.durabilityCorrection = durabilityCorrection;
    }
}