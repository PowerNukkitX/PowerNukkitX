package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ShowCreditsPacket;
import org.jetbrains.annotations.NotNull;

public class ShowCreditsProcessor extends DataPacketProcessor<ShowCreditsPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ShowCreditsPacket pk) {
        if (pk.status == ShowCreditsPacket.STATUS_END_CREDITS) {
            if (playerHandle.getShowingCredits()) {
                playerHandle.player.setShowingCredits(false);
                playerHandle.player.teleport(playerHandle.player.getSpawn().left(), PlayerTeleportEvent.TeleportCause.END_PORTAL);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SHOW_CREDITS_PACKET;
    }
}
