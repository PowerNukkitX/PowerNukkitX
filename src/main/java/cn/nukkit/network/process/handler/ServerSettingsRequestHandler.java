package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsRequestPacket;

/**
 * @author Kaooot
 */
public class ServerSettingsRequestHandler implements PacketHandler<ServerSettingsRequestPacket> {

    @Override
    public void handle(ServerSettingsRequestPacket packet, PlayerSessionHolder holder, Server server) {
        // TODO protocol
    }
}