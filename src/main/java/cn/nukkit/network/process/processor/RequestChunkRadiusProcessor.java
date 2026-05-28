package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import org.jetbrains.annotations.NotNull;

public class RequestChunkRadiusProcessor extends DataPacketProcessor<RequestChunkRadiusPacket> {

    private static final int MIN_CHUNK_RADIUS = 2;
    private static final int MAX_CHUNK_RADIUS = 32;

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestChunkRadiusPacket pk) {
        int radius = Math.max(MIN_CHUNK_RADIUS, Math.min(pk.radius, MAX_CHUNK_RADIUS));
        playerHandle.player.setViewDistance(radius);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;
    }
}