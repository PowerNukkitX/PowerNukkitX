package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PositionTrackingDBClientRequestPacket;
import cn.nukkit.network.protocol.PositionTrackingDBServerBroadcastPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.positiontracking.PositionTracking;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class PositionTrackingDBClientRequestProcessor
        extends DataPacketProcessor<PositionTrackingDBClientRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PositionTrackingDBClientRequestPacket pk) {
        Player player = playerHandle.player;
        try {
            PositionTracking positionTracking =
                    player.getServer().getPositionTrackingService().startTracking(player, pk.getTrackingId(), true);
            if (positionTracking != null) {
                return;
            }
        } catch (IOException e) {
            log.warn("Failed to track the trackingHandler {}", pk.getTrackingId(), e);
        }
        PositionTrackingDBServerBroadcastPacket notFound = new PositionTrackingDBServerBroadcastPacket();
        notFound.setAction(PositionTrackingDBServerBroadcastPacket.Action.NOT_FOUND);
        notFound.setTrackingId(pk.getTrackingId());
        player.dataPacket(notFound);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET);
    }
}
