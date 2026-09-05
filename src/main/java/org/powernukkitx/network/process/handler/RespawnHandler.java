package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.level.Position;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.math.vector.Vector3f;
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

        Position searchingSpawn = player.getRespawnSearchPosition();
        Position resolvedSpawn = player.getRespawnPosition();

        player.setPendingRespawnPosition(resolvedSpawn);

        Position packetSearchingSpawn = searchingSpawn;
        Position packetResolvedSpawn = resolvedSpawn;

        if (resolvedSpawn.getLevel() != player.getLevel()) {
            boolean crossDimension = resolvedSpawn.getLevel().getDimension() != player.getLevel().getDimension();

            if (crossDimension) {
                if (!player.prepareRespawnDimension(searchingSpawn, resolvedSpawn)) {
                    packetSearchingSpawn = player.getPosition();
                    packetResolvedSpawn = player.getPosition();
                }
            } else {
                packetSearchingSpawn = player.getPosition();
                packetResolvedSpawn = player.getPosition();
            }
        }

        final RespawnPacket searchingPacket = new RespawnPacket();
        searchingPacket.setPosition(Vector3f.from(
                packetSearchingSpawn.x,
                32767,
                packetSearchingSpawn.z
        ));
        searchingPacket.setState(PlayerRespawnState.SEARCHING_FOR_SPAWN);
        searchingPacket.setPlayerRuntimeId(0);

        player.sendPacket(searchingPacket);

        final RespawnPacket readyPacket = new RespawnPacket();
        readyPacket.setPosition(Vector3f.from(
                packetResolvedSpawn.x,
                packetResolvedSpawn.y + player.getEyeHeight(),
                packetResolvedSpawn.z
        ));
        readyPacket.setState(PlayerRespawnState.READY_TO_SPAWN);
        readyPacket.setPlayerRuntimeId(0);

        player.sendPacket(readyPacket);
    }
}
