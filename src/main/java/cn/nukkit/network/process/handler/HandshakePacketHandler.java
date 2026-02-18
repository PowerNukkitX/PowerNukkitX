package cn.nukkit.network.process.handler;

import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.SessionState;
import org.cloudburstmc.protocol.bedrock.packet.ClientToServerHandshakePacket;
import org.cloudburstmc.protocol.common.PacketSignal;

public class HandshakePacketHandler extends BedrockSessionPacketHandler {
    public HandshakePacketHandler(BedrockSession session) {
        super(session);
    }

    @Override
    public PacketSignal handle(ClientToServerHandshakePacket pk) {
        session.getMachine().fire(SessionState.RESOURCE_PACK);
        return PacketSignal.HANDLED;
    }
}
