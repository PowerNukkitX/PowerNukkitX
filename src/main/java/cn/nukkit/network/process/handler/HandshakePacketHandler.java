package cn.nukkit.network.process.handler;

import cn.nukkit.network.process.NetworkSessionState;
import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.protocol.ClientToServerHandshakePacket;

public class HandshakePacketHandler extends NetworkSessionPacketHandler {
    public HandshakePacketHandler(NetworkSession session) {
        super(session);
    }

    @Override
    public void handle(ClientToServerHandshakePacket pk) {
        session.getMachine().fire(NetworkSessionState.RESOURCE_PACK);
    }
}
