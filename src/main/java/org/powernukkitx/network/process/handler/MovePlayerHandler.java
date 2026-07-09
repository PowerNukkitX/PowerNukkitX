package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.level.Location;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;

/**
 * @author Kaooot
 */
public class MovePlayerHandler implements PacketHandler<MovePlayerPacket> {

    @Override
    public void handle(MovePlayerPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
            return;
        }
        if (!playerHandle.packetRateLimiter.tryMovement()) {
            return;
        }
        Vector3 newPos = new Vector3(packet.getPosition().getX(), packet.getPosition().getY() - playerHandle.getBaseOffset(), packet.getPosition().getZ());

        float pitch = packet.getRotation().getX();
        float yaw = packet.getRotation().getY();
        float headYaw = packet.getRotation().getZ();
        yaw %= 360;
        headYaw %= 360;
        pitch %= 360;
        if (packet.getRotation().getY() < 0) {
            yaw += 360;
        }
        if (packet.getRotation().getZ() < 0) {
            headYaw += 360;
        }
        playerHandle.offerMovementTask(Location.fromObject(newPos, player.level, yaw, pitch, headYaw));
    }
}