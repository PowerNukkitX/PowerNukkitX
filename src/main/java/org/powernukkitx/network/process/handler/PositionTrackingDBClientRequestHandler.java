package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.positiontracking.PositionTracking;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.PositionTrackingDBClientRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.PositionTrackingDBServerBroadcastPacket;

import java.io.IOException;

/**
 * @author Kaooot
 */
@Slf4j
public class PositionTrackingDBClientRequestHandler implements PacketHandler<PositionTrackingDBClientRequestPacket> {

    @Override
    public void handle(PositionTrackingDBClientRequestPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        try {
            PositionTracking positionTracking = player.getServer().getPositionTrackingService().startTracking(player, packet.getTrackingId(), true);
            if (positionTracking != null) {
                return;
            }
        } catch (IOException e) {
            log.warn("Failed to track the trackingHandler {}", packet.getTrackingId(), e);
        }
        PositionTrackingDBServerBroadcastPacket notFound = new PositionTrackingDBServerBroadcastPacket();
        notFound.setAction(PositionTrackingDBServerBroadcastPacket.Action.NOT_FOUND);
        notFound.setTrackingId(packet.getTrackingId());
        player.sendPacket(notFound);
    }
}