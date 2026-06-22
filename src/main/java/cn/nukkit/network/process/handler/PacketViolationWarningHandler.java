package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
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