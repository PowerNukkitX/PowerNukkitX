package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.RespawnPacket;
import org.jetbrains.annotations.NotNull;

public class RespawnProcessor extends DataPacketProcessor<RespawnPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RespawnPacket pk) {
        Player player = playerHandle.player;
        if (player.isAlive()) {
            return;
        }
        if (pk.getState() == RespawnPacket.State.CLIENT_READY) {
            RespawnPacket respawn1 = new RespawnPacket();
            respawn1.setPosition(Vector3f.from((float) player.getX(), (float) player.getY(), (float) player.getZ()));
            respawn1.setState(RespawnPacket.State.SERVER_READY);
            respawn1.setRuntimeEntityId(player.getId());
            player.dataPacket(respawn1);
        }
    }
    @Override
    public Class<RespawnPacket> getPacketClass() {
        return RespawnPacket.class;
    }
}
