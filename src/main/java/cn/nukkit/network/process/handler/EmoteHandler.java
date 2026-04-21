package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.utils.UUIDValidator;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.EmoteFlag;
import org.cloudburstmc.protocol.bedrock.packet.EmotePacket;

/**
 * @author Kaooot
 */
@Slf4j
public class EmoteHandler implements PacketHandler<EmotePacket> {

    @Override
    public void handle(EmotePacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        if (!playerHandle.player.spawned) {
            return;
        }
        if (packet.getActorRuntimeId() != playerHandle.player.getId()) {
            log.warn("{} sent EmotePacket with invalid entity id: {} != {}", playerHandle.getUsername(), packet.getActorRuntimeId(), playerHandle.player.getId());
            return;
        }
        if (!UUIDValidator.isValidUUID(packet.getEmoteId())) {
            log.warn("{} sent EmotePacket with invalid emoteId: {}", playerHandle.getUsername(), packet.getEmoteId());
            return;
        }

        packet.getFlags().clear();
        packet.getFlags().add(EmoteFlag.SERVER_SIDE);
        packet.getFlags().add(EmoteFlag.MUTE_EMOTE_CHAT);
        for (Player viewer : playerHandle.player.getViewers().values()) {
            viewer.sendPacket(packet);
        }
    }
}