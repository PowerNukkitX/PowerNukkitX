package cn.nukkit.network.process.processor;

import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.EmotePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class EmoteProcessor extends DataPacketProcessor<EmotePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull EmotePacket pk) {
        if (!playerHandle.player.isSpawned()) {
            return;
        }
        if (pk.runtimeId != playerHandle.player.getId()) {
            log.warn(
                    "{} sent EmotePacket with invalid entity id: {} != {}",
                    playerHandle.getUsername(),
                    pk.runtimeId,
                    playerHandle.player.getId());
            return;
        }
        for (Player viewer : playerHandle.player.getViewers().values()) {
            viewer.sendPacket(pk);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.EMOTE_PACKET);
    }
}
