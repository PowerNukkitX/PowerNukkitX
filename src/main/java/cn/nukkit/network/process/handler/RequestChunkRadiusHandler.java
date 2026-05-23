package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.network.process.SessionState;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;

/**
 * @author Kaooot
 */
public class RequestChunkRadiusHandler implements PacketHandler<RequestChunkRadiusPacket> {

    private static final int MIN_CHUNK_RADIUS = 2;
    private static final int MAX_CHUNK_RADIUS = 32;

    @Override
    public void handle(RequestChunkRadiusPacket packet, PlayerSessionHolder holder, Server server) {
        if (!holder.getState().equals(SessionState.CHUNKS)) {
            holder.disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
            return;
        }
        final int radius = Math.max(MIN_CHUNK_RADIUS, Math.min(packet.getChunkRadius(), MAX_CHUNK_RADIUS));
        holder.getPlayer().setViewDistance(radius);
    }
}