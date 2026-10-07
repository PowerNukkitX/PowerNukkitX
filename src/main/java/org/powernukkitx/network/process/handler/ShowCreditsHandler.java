package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerTeleportEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.utils.PortalHelper;
import org.cloudburstmc.protocol.bedrock.packet.ShowCreditsPacket;

/**
 * @author Kaooot
 */
public class ShowCreditsHandler implements PacketHandler<ShowCreditsPacket> {

    @Override
    public void handle(ShowCreditsPacket packet, PlayerSessionHolder holder, Server server) {
        if (packet.getCreditsState().equals(ShowCreditsPacket.CreditsState.END_CREDITS)) {
            PlayerHandle playerHandle = holder.getPlayerHandle();
            if (playerHandle.getShowingCredits()) {
                playerHandle.player.setShowingCredits(false);
                playerHandle.player.setHasSeenCredits(true);
                Position spawn;
                if (playerHandle.player.getSpawn().right() == Player.SpawnPointType.WORLD) {
                    Level pendingDestination = playerHandle.getPendingEndPortalDestination();
                    spawn = pendingDestination != null
                            ? PortalHelper.convertPosBetweenEndAndOverworld(playerHandle.player.getLocation(), pendingDestination)
                            : PortalHelper.convertPosBetweenEndAndOverworld(playerHandle.player.getLocation());
                } else spawn = playerHandle.player.getSpawn().left();
                playerHandle.setPendingEndPortalDestination(null);
                if (spawn != null) {
                    playerHandle.player.teleport(spawn, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                }
            }
        }
    }
}