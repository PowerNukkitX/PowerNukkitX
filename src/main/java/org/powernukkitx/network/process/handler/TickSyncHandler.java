package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.TickSyncPacket;

/**
 * @author Kaooot
 */
public class TickSyncHandler implements PacketHandler<TickSyncPacket> {

    @Override
    public boolean runsOnNetworkThread() {
        return true;
    }

    @Override
    public void handle(TickSyncPacket packet, PlayerSessionHolder holder, Server server) {
        final TickSyncPacket responsePacket = new TickSyncPacket();
        responsePacket.setRequestTimestamp(packet.getRequestTimestamp());
        responsePacket.setResponseTimestamp(server.getTick());
        holder.getPlayer().sendPacketImmediately(responsePacket);
    }
}