package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class MovePlayerProcessor extends DataPacketProcessor<MovePlayerPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull MovePlayerPacket pk) {
        Player player = playerHandle.player;
        if (/*!player.locallyInitialized || */Server.getInstance().getServerAuthoritativeMovement() > 0) {
            return;
        }
        Vector3 newPos = new Vector3(pk.x, pk.y - playerHandle.getBaseOffset(), pk.z);

        pk.yaw %= 360;
        pk.headYaw %= 360;
        pk.pitch %= 360;
        if (pk.yaw < 0) {
            pk.yaw += 360;
        }
        if (pk.headYaw < 0) {
            pk.headYaw += 360;
        }
        playerHandle.offerMovementTask(Location.fromObject(newPos, player.level, pk.yaw, pk.pitch, pk.headYaw));
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.MOVE_PLAYER_PACKET;
    }
}
