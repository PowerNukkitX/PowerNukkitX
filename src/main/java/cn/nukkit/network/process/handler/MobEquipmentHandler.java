package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
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

        if (packet.getSelectedSlot() < 0 || packet.getSelectedSlot() > 8) {
            player.close("§cPacket handling error");
            return;
        }
        final Item itemFromNetwork = Item.fromNetwork(packet.getItem());
        if (!itemFromNetwork.isNull()) {
            if (itemFromNetwork.getEnchantments().length > Enchantment.getEnchantments().length) { // Last Enchant Id
                player.close("§cPacket handling error");
                return;
            }
            if (itemFromNetwork.getLore().length > 100) {
                player.close("§cPacket handling error");
                return;
            }
            if (itemFromNetwork.getCanPlaceOn().size() > 250) {
                player.close("§cPacket handling error");
                return;
            }
            if (itemFromNetwork.getCanDestroy().size() > 250) {
                player.close("§cPacket handling error");
                return;
            }
        }

        Inventory inv = player.getWindowById(packet.getContainerId());

        if (inv == null) {
            log.debug("Player {} has no open container with window ID {}", player.getName(), packet.getContainerId());
            return;
        }

        if (inv instanceof HumanInventory inventory && inventory.getHeldItemIndex() == packet.getSelectedSlot()) {
            return;
        }

        Item item = inv.getItem(packet.getSelectedSlot());

        if (!item.equals(itemFromNetwork, false, true)) {
            Item fixItem = Item.get(item.getId(), item.getDamage(), item.getCount(), item.getCompoundTag());
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