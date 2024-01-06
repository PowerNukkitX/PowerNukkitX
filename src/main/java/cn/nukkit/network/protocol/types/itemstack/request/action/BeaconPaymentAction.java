package cn.nukkit.network.protocol.types.itemstack.request.action;

import lombok.Value;

/**
 * BeaconPaymentStackRequestActionData is sent by the client when it submits an item to enable effects from a
 * beacon. These items will have been moved into the beacon item slot in advance.
 */
@Value
public class BeaconPaymentAction implements ItemStackRequestAction {
    int primaryEffect;
    int secondaryEffect;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.BEACON_PAYMENT;
    }
}
