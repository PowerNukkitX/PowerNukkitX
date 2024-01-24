package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RespawnPacket;
import org.jetbrains.annotations.NotNull;

public class RespawnProcessor extends DataPacketProcessor<RespawnPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RespawnPacket pk) {
        Player player = playerHandle.player;
        if (player.isAlive()) {
            return;
        }
        if (pk.respawnState == RespawnPacket.STATE_CLIENT_READY_TO_SPAWN) {
            RespawnPacket respawn1 = new RespawnPacket();
            respawn1.x = (float) player.getX();
            respawn1.y = (float) player.getY();
            respawn1.z = (float) player.getZ();
            respawn1.respawnState = RespawnPacket.STATE_READY_TO_SPAWN;
            player.dataPacket(respawn1);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.RESPAWN_PACKET;
    }
}
