package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.packet.PlayerHotbarPacket;

/**
 * @author Kaooot
 */
public class PlayerHotbarHandler implements PacketHandler<PlayerHotbarPacket> {

    @Override
    public void handle(PlayerHotbarPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (player == null || !player.spawned || !player.isAlive()) {
            return;
        }
        if (packet.getContainerID() != ContainerId.INVENTORY) {
            return; //In PE this should never happen
        }
        int slot = packet.getSelectedSlot();
        if (slot < 0 || slot >= player.getInventory().getSize()) {
            return;
        }
        player.getInventory().equipItem(slot);
    }
}