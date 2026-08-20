package org.powernukkitx.network.process.handler;

import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.packet.PlayerHotbarPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class PlayerHotbarHandler implements PacketHandler<PlayerHotbarPacket> {

    @Override
    public void handle(PlayerHotbarPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();
        if (player == null || !player.spawned || !player.isAlive()){
            return;
        }
        if (packet.getContainerID() != ContainerId.INVENTORY) {
            return; //In PE this should never happen
        }
        int selectedSlot = packet.getSelectedSlot();
        if (selectedSlot < 0 || selectedSlot >= player.getInventory().getHotbarSize()){
            log.debug("Player {} sent invalid hotbar slot {}", player.getName(), selectedSlot);
            return;
        }

        player.getInventory().equipItem(selectedSlot);
    }
}
