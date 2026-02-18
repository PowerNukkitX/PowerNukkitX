package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Position;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.ShowCreditsPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetSpawnPositionPacket.Type;
import cn.nukkit.utils.PortalHelper;
import org.jetbrains.annotations.NotNull;

public class ShowCreditsProcessor extends DataPacketProcessor<ShowCreditsPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ShowCreditsPacket pk) {
        if (pk.getStatus() == ShowCreditsPacket.Status.END_CREDITS) {
            if (playerHandle.getShowingCredits()) {
                playerHandle.player.setShowingCredits(false);
                Position spawn;
                if(playerHandle.player.getSpawn().right() == Type.WORLD_SPAWN) {
                    spawn = PortalHelper.convertPosBetweenEndAndOverworld(playerHandle.player.getLocation());
                } else spawn = playerHandle.player.getSpawn().left();
                if(spawn != null) {
                    playerHandle.player.teleport(spawn, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                }
            }
        }
    }
    @Override
    public Class<ShowCreditsPacket> getPacketClass() {
        return ShowCreditsPacket.class;
    }
}
