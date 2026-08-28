package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.PacketViolationWarningPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class PacketViolationWarningHandler implements PacketHandler<PacketViolationWarningPacket> {

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public void handle(PacketViolationWarningPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();
        log.warn("Violation warning from {}: {}",
                player != null ? player.getName() : holder.getSession().getSocketAddress(), packet);
    }
}