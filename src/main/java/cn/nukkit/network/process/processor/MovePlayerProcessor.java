package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.jetbrains.annotations.NotNull;

public class MovePlayerProcessor extends DataPacketProcessor<MovePlayerPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MovePlayerPacket pk) {
        Player player = playerHandle.player;
        if (Server.getInstance().getServerAuthoritativeMovement() > 0) {
            return;
        }
        Vector3 newPos = new Vector3(pk.getPosition().getX(), pk.getPosition().getY() - playerHandle.getBaseOffset(), pk.getPosition().getZ());

        float yaw = pk.getRotation().getY() % 360;
        float headYaw = pk.getRotation().getZ() % 360;
        float pitch = pk.getRotation().getX() % 360;
        if (yaw < 0) {
            yaw += 360;
        }
        if (headYaw < 0) {
            headYaw += 360;
        }
        playerHandle.offerMovementTask(Location.fromObject(newPos, player.level, yaw, pitch, headYaw));
    }

    @Override
    public Class<MovePlayerPacket> getPacketClass() {
        return MovePlayerPacket.class;
    }
}
