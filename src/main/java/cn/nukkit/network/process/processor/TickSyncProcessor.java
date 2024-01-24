package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.TickSyncPacket;
import org.jetbrains.annotations.NotNull;

public class TickSyncProcessor extends DataPacketProcessor<TickSyncPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull TickSyncPacket pk) {
        TickSyncPacket tickSyncPacketToClient = new TickSyncPacket();
        tickSyncPacketToClient.setRequestTimestamp(pk.getRequestTimestamp());
        tickSyncPacketToClient.setResponseTimestamp(playerHandle.player.getServer().getTick());
        playerHandle.player.dataPacketImmediately(tickSyncPacketToClient);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.TICK_SYNC_PACKET;
    }
}
