package cn.nukkit.network.process;

import cn.nukkit.Server;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;

/**
 * @author Kaooot
 */
@RequiredArgsConstructor
public class NetworkPacketHandler implements BedrockPacketHandler {

    private final Server server;
    private final PlayerSessionHolder session;

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        final PacketHandler packetHandler;
        if ((packetHandler = PacketHandlerRegistry.getPacketHandler(packet.getClass())) != null) {
            packetHandler.handle(packet, this.session, this.server);
            return PacketSignal.HANDLED;
        } /*else {
            this.server.getLogger().warning("Unimplemented handler for packet: " + packet.getPacketType());
        }*/
        return BedrockPacketHandler.super.handlePacket(packet);
    }
}