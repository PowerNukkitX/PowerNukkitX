package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerTeleportEvent;
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
        PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }
        if (packet.getCreditsState().equals(ShowCreditsPacket.CreditsState.END_CREDITS)) {
            if (playerHandle.getShowingCredits()) {
                player.setShowingCredits(false);
                Position spawn;
                if (player.getSpawn().right() == Player.SpawnPointType.WORLD) {
                    spawn = PortalHelper.convertPosBetweenEndAndOverworld(player.getLocation());
                } else spawn = player.getSpawn().left();
                if (spawn != null) {
                    player.teleport(spawn, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                }
            }
        }
    }
}
