package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.PlayerInputPacket;
import org.jetbrains.annotations.NotNull;

public class PlayerInputProcessor extends DataPacketProcessor<PlayerInputPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerInputPacket pk) {
        if (!playerHandle.player.isAlive() || !playerHandle.player.spawned) {
            return;
        }
        if (playerHandle.player.riding instanceof EntityMinecartAbstract entityMinecartAbstract) {
            entityMinecartAbstract.setCurrentSpeed(pk.getInputMotion().getY());
        }
    }

    @Override
    public Class<PlayerInputPacket> getPacketClass() {
        return PlayerInputPacket.class;
    }
}
