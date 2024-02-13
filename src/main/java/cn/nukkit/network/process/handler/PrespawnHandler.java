package cn.nukkit.network.process.handler;

import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.process.NetworkSessionState;
import cn.nukkit.network.protocol.ChunkRadiusUpdatedPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;

public class PrespawnHandler extends NetworkSessionPacketHandler {
    public PrespawnHandler(NetworkSession session) {
        super(session);
    }

    @Override
    public void handle(RequestChunkRadiusPacket pk) {
        ChunkRadiusUpdatedPacket packet = new ChunkRadiusUpdatedPacket();
        handle.setChunkRadius(Math.max(2, Math.min(pk.radius, handle.player.getViewDistance())));
        packet.radius = handle.getChunkRadius();
        session.sendDataPacket(packet);
    }


    @Override
    public void handle(SetLocalPlayerAsInitializedPacket pk) {
        this.session.getMachine().fire(NetworkSessionState.IN_GAME);
    }
}
