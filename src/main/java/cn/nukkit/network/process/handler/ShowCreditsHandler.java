package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Position;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.utils.PortalHelper;
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
                Position spawn;
                if (playerHandle.player.getSpawn().right() == Player.SpawnPointType.WORLD) {
                    spawn = PortalHelper.convertPosBetweenEndAndOverworld(playerHandle.player.getLocation());
                } else spawn = playerHandle.player.getSpawn().left();
                if (spawn != null) {
                    playerHandle.player.teleport(spawn, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                }
            }
        }
    }
}