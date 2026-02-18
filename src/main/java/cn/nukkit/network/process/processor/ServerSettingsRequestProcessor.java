package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerServerSettingsRequestEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsResponsePacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ServerSettingsRequestProcessor extends DataPacketProcessor<ServerSettingsRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ServerSettingsRequestPacket pk) {
        PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(playerHandle.player, new HashMap<>(playerHandle.getServerSettings()));
        playerHandle.player.getServer().getPluginManager().callEvent(settingsRequestEvent);

        if (!settingsRequestEvent.isCancelled()) {
            settingsRequestEvent.getSettings().forEach((id, window) -> {
                ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                re.setFormId(id);
                re.setFormData(window.toJson());
                playerHandle.player.dataPacket(re);
            });
        }
    }
    @Override
    public Class<ServerSettingsRequestPacket> getPacketClass() {
        return ServerSettingsRequestPacket.class;
    }
}
