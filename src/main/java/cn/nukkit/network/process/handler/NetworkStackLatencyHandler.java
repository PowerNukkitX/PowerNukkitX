package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.NetworkStackLatencyPacket;

/**
 * @author Kaooot
 */
public class NetworkStackLatencyHandler implements PacketHandler<NetworkStackLatencyPacket> {

    @Override
    public void handle(NetworkStackLatencyPacket packet, PlayerSessionHolder holder, Server server) {
        final long serverTime = holder.getPlayerHandle().getLastServerNetworkStackLatencyTimeInMS();
        holder.getPlayerHandle().setLatencyTimeInMS(serverTime - (packet.getCreationTime() / 1_000_000L));
    }
}