package cn.nukkit.network.process.processor;

import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PlayerInputPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class PlayerInputProcessor extends DataPacketProcessor<PlayerInputPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerInputPacket pk) {
        Player player = playerHandle.getPlayer();
        if (!player.isAlive() || !player.isSpawned()) {
            return;
        }
        if (player.riding instanceof EntityMinecartAbstract entityMinecartAbstract) {
            entityMinecartAbstract.setCurrentSpeed(pk.motionY);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.PLAYER_INPUT_PACKET);
    }
}
