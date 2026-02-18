package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.EmotePacket;
import cn.nukkit.utils.UUIDValidator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EmoteProcessor extends DataPacketProcessor<EmotePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull EmotePacket pk) {
        if (!playerHandle.player.spawned) {
            return;
        }
        if (pk.getRuntimeEntityId() != playerHandle.player.getId()) {
            log.warn("{} sent EmotePacket with invalid entity id: {} != {}", playerHandle.getUsername(), pk.getRuntimeEntityId(), playerHandle.player.getId());
            return;
        }
        if (!UUIDValidator.isValidUUID(pk.getEmoteId())) {
            log.warn("{} sent EmotePacket with invalid emoteId: {}", playerHandle.getUsername(), pk.getEmoteId());
            return;
        }

        for (Player viewer : playerHandle.player.getViewers().values()) {
            viewer.dataPacket(pk);
        }
    }

    @Override
    public Class<EmotePacket> getPacketClass() {
        return EmotePacket.class;
    }
}
