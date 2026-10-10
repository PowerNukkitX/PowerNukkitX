package org.powernukkitx.network.process.handler;

import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;

/**
 * @author Kaooot
 */
public class RequestChunkRadiusHandler implements PacketHandler<RequestChunkRadiusPacket> {

    @Override
    public void handle(RequestChunkRadiusPacket packet, PlayerSessionHolder holder, Server server) {
        holder.getPlayer().setClientRequestedChunkRadius(
            packet.getChunkRadius()
        );
    }
}
