package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.level.Position;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.PlayerRespawnState;
import org.cloudburstmc.protocol.bedrock.packet.RespawnPacket;

/**
 * @author Kaooot
 */
public class RespawnHandler implements PacketHandler<RespawnPacket> {

    @Override
    public void handle(RespawnPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (!packet.getState().equals(PlayerRespawnState.CLIENT_READY_TO_SPAWN) || player.isAlive()) {
            return;
        }
        Position spawn = player.getRespawnPosition();
        if (spawn.getLevel() != player.getLevel()) {
            // RespawnPacket carries no dimension, so a position in another level would be applied to the dimension
            // Player#respawn then handles the dimension change
            spawn = player.getPosition();
        }

        final RespawnPacket respawnPacket = new RespawnPacket();
        respawnPacket.setPosition(spawn.toNetwork());
        respawnPacket.setState(PlayerRespawnState.READY_TO_SPAWN);
        respawnPacket.setPlayerRuntimeId(player.getId());

        player.sendPacket(respawnPacket);
    }
}
