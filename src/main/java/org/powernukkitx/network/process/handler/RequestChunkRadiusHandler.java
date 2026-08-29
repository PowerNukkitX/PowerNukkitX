package org.powernukkitx.network.process.handler;

import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;

/**
 * @author Kaooot
 */
public class RequestChunkRadiusHandler implements PacketHandler<RequestChunkRadiusPacket> {

    private static final int MIN_CHUNK_RADIUS = 2;

    @Override
    public void handle(RequestChunkRadiusPacket packet, PlayerSessionHolder holder, Server server) {
        final int viewDistance = server.getSettings().gameplaySettings().viewDistance();
        final int radius = Math.max(MIN_CHUNK_RADIUS, Math.min(packet.getChunkRadius(), viewDistance));
        holder.getPlayer().setViewDistance(radius);
    }
}
