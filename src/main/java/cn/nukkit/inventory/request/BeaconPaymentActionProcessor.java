package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.network.protocol.types.itemstack.request.action.BeaconPaymentAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class BeaconPaymentActionProcessor implements ItemStackRequestActionProcessor<BeaconPaymentAction> {
    @Override
    public ActionResponse handle(BeaconPaymentAction action, Player player, ItemStackRequestContext context) {
        Optional<Inventory> topWindow = player.getTopWindow();
        if (topWindow.isEmpty()) {
            log.error("the player's haven't open any inventory!");
            return context.error();
        }
        if (!(topWindow.get() instanceof BeaconInventory beaconInventory)) {
            log.error("the player's haven't open beacon inventory!");
            return context.error();
        }
        BlockEntityBeacon holder = beaconInventory.getHolder();
        holder.setPrimaryPower(action.getPrimaryEffect());
        holder.setSecondaryPower(action.getSecondaryEffect());
        return null;
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.BEACON_PAYMENT;
    }
}
