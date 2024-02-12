package cn.nukkit.network.process.handler;

import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.protocol.ClientToServerHandshakePacket;

public class HandshakePacketHandler extends NetworkSessionPacketHandler {

    private final Runnable onHandshakeCompleted;

    public HandshakePacketHandler(NetworkSession session, Runnable onHandshakeCompleted) {
        super(session);
        this.onHandshakeCompleted = onHandshakeCompleted;
    }

    @Override
    public void handle(ClientToServerHandshakePacket pk) {
        this.onHandshakeCompleted.run();
    }
}
