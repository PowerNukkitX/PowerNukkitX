package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.TickSyncPacket;

/**
 * @author Kaooot
 */
public class TickSyncHandler implements PacketHandler<TickSyncPacket> {

    @Override
    public void handle(TickSyncPacket packet, PlayerSessionHolder holder, Server server) {
        final TickSyncPacket responsePacket = new TickSyncPacket();
        responsePacket.setRequestTimestamp(packet.getRequestTimestamp());
        responsePacket.setResponseTimestamp(server.getTick());
        holder.getPlayer().dataPacketImmediately(responsePacket);
    }
}