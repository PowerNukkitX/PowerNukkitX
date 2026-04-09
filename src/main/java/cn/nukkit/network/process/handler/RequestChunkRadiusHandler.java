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

    @Override
    public void handle(RequestChunkRadiusPacket packet, PlayerSessionHolder holder, Server server) {
        if (!holder.getState().equals(SessionState.CHUNKS)) {
            holder.disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
            return;
        }
        holder.getPlayer().setViewDistance(Math.max(2, holder.getPlayer().getViewDistance()));
    }
}