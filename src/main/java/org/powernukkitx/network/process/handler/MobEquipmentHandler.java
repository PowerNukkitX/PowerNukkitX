package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class MobEquipmentHandler implements PacketHandler<MobEquipmentPacket> {

    @Override
    public void handle(MobEquipmentPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (!player.getInventory().isHotbarSlot(packet.getSelectedSlot())) {
            log.warn("Player {} sent a MobEquipmentPacket with an invalid selected slot: {}", player.getName(), packet.getSelectedSlot());
            return;
        }

        Inventory inv = player.getWindowById(packet.getContainerId());

        if (inv == null) {
            log.debug("Player {} has no open container with window ID {}", player.getName(), packet.getContainerId());
            return;
        }

        if (inv instanceof HumanInventory inventory && inventory.getHeldItemIndex() == packet.getSelectedSlot()) {
            return;
        }

        final Item itemFromNetwork = Item.fromNetwork(packet.getItem());
        if (!itemFromNetwork.isNull()) {
            if (itemFromNetwork.getEnchantments().length > Enchantment.getEnchantments().length) { // Last Enchant Id
                log.warn("Player {} sent a MobEquipmentPacket with an invalid enchantment count: {}", player.getName(), itemFromNetwork.getEnchantments().length);
                return;
            }
            if (itemFromNetwork.getLore().length > 100) {
                log.warn("Player {} sent a MobEquipmentPacket with an invalid lore count: {}", player.getName(), itemFromNetwork.getLore().length);
                return;
            }
            if (itemFromNetwork.getCanPlaceOn().size() > 250) {
                log.warn("Player {} sent a MobEquipmentPacket with an invalid canPlaceOn count: {}", player.getName(), itemFromNetwork.getCanPlaceOn().size());
                return;
            }
            if (itemFromNetwork.getCanDestroy().size() > 250) {
                log.warn("Player {} sent a MobEquipmentPacket with an invalid canDestroy count: {}", player.getName(), itemFromNetwork.getCanDestroy().size());
                return;
            }
        }

        Item item = inv.getItem(packet.getSelectedSlot());
        if (!item.equals(itemFromNetwork, false, true)) {
            Item fixItem = Item.get(item.getId(), item.getDamage(), item.getCount(), item.getNbtBytes());
            if (fixItem.equals(itemFromNetwork, false, true)) {
                inv.setItem(packet.getSelectedSlot(), fixItem);
            } else {
                log.debug("Tried to equip {} but have {} in target slot", itemFromNetwork, fixItem);
                inv.sendContents(player);
            }
        }

        if (inv instanceof HumanInventory inventory) {
            inventory.equipItem(packet.getSelectedSlot());
        }

        player.setDataFlag(ActorFlags.USING_ITEM, false);
    }
}
