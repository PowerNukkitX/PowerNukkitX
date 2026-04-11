package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.ServerboundDiagnosticsPacket;

/**
 * @author Kaooot
 */
public class ServerboundDiagnosticsHandler implements PacketHandler<ServerboundDiagnosticsPacket> {

    @Override
    public void handle(ServerboundDiagnosticsPacket packet, PlayerSessionHolder holder, Server server) {

    }
}