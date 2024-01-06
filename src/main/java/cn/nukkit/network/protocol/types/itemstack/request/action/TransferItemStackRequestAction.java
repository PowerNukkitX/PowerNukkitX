package cn.nukkit.network.protocol.types.itemstack.request.action;


import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;

/**
 * TransferStackRequestActionData is the structure shared by StackRequestActions that transfer items from one
 * slot into another
 */
public interface TransferItemStackRequestAction extends ItemStackRequestAction {

    int getCount();

    ItemStackRequestSlotData getSource();

    ItemStackRequestSlotData getDestination();
}
