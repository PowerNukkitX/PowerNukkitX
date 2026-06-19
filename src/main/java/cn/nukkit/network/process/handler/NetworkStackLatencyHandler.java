package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.NetworkStackLatencyPacket;

/**
 * @author Kaooot
 */
public class NetworkStackLatencyHandler implements PacketHandler<NetworkStackLatencyPacket> {

    private static final long MAX_PLAUSIBLE_PING_MS = 60_000L;

    @Override
    public void handle(NetworkStackLatencyPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();

        playerHandle.onAckReceive(packet.getCreationTime());

        if (packet.isFromServer()) {
            final NetworkStackLatencyPacket response = new NetworkStackLatencyPacket();
            response.setCreationTime(packet.getCreationTime());
            response.setFromServer(false);
            holder.getSession().sendPacketImmediately(response);
            return;
        }

        final long latency = System.currentTimeMillis() - (packet.getCreationTime() / 1_000_000L);
        if (latency >= 0 && latency <= MAX_PLAUSIBLE_PING_MS) {
            playerHandle.setLatencyTimeInMS(latency);
        }
    }
}
