package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import org.jetbrains.annotations.NotNull;

public class RequestChunkRadiusProcessor extends DataPacketProcessor<RequestChunkRadiusPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestChunkRadiusPacket pk) {
        playerHandle.player.setViewDistance(pk.radius);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;
    }
}
