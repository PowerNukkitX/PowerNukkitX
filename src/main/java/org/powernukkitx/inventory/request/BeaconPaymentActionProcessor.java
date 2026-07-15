package org.powernukkitx.inventory.request;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntityBeacon;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.inventory.BeaconInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.BeaconPaymentAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class BeaconPaymentActionProcessor implements ItemStackRequestActionProcessor<BeaconPaymentAction> {
    private static final Set<String> VALID_PAYMENT_ITEMS = Set.of(
            Item.IRON_INGOT,
            Item.GOLD_INGOT,
            Item.DIAMOND,
            Item.EMERALD,
            Item.NETHERITE_INGOT
    );

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
        Item payment = beaconInventory.getItem(0);
        if (payment.isNull() || !VALID_PAYMENT_ITEMS.contains(payment.getId())) {
            log.warn("invalid beacon payment item {}!", payment);
            return context.error();
        }
        BlockEntityBeacon holder = beaconInventory.getHolder();
        int powerLevel = holder.getPowerLevel();
        int primary = action.getPrimaryEffectId();
        int secondary = action.getSecondaryEffectId();
        if (!BlockEntityBeacon.isPrimaryAllowed(primary, powerLevel)) {
            log.warn("beacon primary effect {} is not allowed for power level {}!", primary, powerLevel);
            return context.error();
        }
        if (secondary != 0 && secondary != primary && secondary != EffectType.REGENERATION.id()) {
            log.warn("invalid beacon secondary effect {}!", secondary);
            return context.error();
        }
        holder.setPrimaryPower(primary);
        holder.setSecondaryPower(secondary);
        return null;
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.SCREEN_BEACON_PAYMENT;
    }
}
