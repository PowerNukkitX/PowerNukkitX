package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.EmotePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.UUIDValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EmoteProcessor extends DataPacketProcessor<EmotePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull EmotePacket pk) {
        if (!playerHandle.player.spawned) {
            return;
        }
        if (pk.runtimeId != playerHandle.player.getId()) {
            log.warn("{} sent EmotePacket with invalid entity id: {} != {}", playerHandle.getUsername(), pk.runtimeId, playerHandle.player.getId());
            return;
        }
        if(!UUIDValidator.isValidUUID(pk.emoteID)) {
            log.warn("{} sent EmotePacket with invalid emoteId: {}", playerHandle.getUsername(), pk.emoteID);
            return;
        }

        for (Player viewer : playerHandle.player.getViewers().values()) {
            viewer.dataPacket(pk);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.EMOTE_PACKET;
    }
}
