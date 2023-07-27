package cn.nukkit.network.process.processor;

import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ShowCreditsPacket;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class ShowCreditsProcessor extends DataPacketProcessor<ShowCreditsPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ShowCreditsPacket pk) {
        if (pk.status == ShowCreditsPacket.STATUS_END_CREDITS) {
            if (playerHandle.getShowingCredits()) {
                playerHandle.player.setShowingCredits(false);
                playerHandle.player.teleport(
                        playerHandle.player.getSpawn(), PlayerTeleportEvent.TeleportCause.END_PORTAL);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.SHOW_CREDITS_PACKET);
    }
}
