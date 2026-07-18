package org.powernukkitx.network.process.handler;

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
    public void handle(PacketViolationWarningPacket packet, PlayerSessionHolder holder, Server server) {
        log.warn("Violation warning from {}: {}", holder.getPlayer().getName(), packet);
    }
}