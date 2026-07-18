package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.ServerboundDiagnosticsPacket;

/**
 * @author Kaooot
 */
public class ServerboundDiagnosticsHandler implements PacketHandler<ServerboundDiagnosticsPacket> {

    @Override
    public void handle(ServerboundDiagnosticsPacket packet, PlayerSessionHolder holder, Server server) {

    }
}