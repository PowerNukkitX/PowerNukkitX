package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkRequestPacket;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/06/01
 */
public class ResourcePackChunkRequestHandler implements PacketHandler<ResourcePackChunkRequestPacket> {

    @Override
    public void handle(ResourcePackChunkRequestPacket packet, PlayerSessionHolder holder, Server server) {
        if (!holder.getState().equals(SessionState.RESOURCE_PACK)) {
            holder.disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
            return;
        }

        holder.getInternalPackManager().handleChunkRequest(packet);
    }
}
