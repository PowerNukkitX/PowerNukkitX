package cn.nukkit.network.process.processor;

import cn.nukkit.event.player.PlayerServerSettingsRequestEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ServerSettingsRequestPacket;
import cn.nukkit.network.protocol.ServerSettingsResponsePacket;
import cn.nukkit.player.PlayerHandle;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

public class ServerSettingsRequestProcessor extends DataPacketProcessor<ServerSettingsRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ServerSettingsRequestPacket pk) {
        PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(
                playerHandle.player, new HashMap<>(playerHandle.getServerSettings()));
        settingsRequestEvent.call();

        if (!settingsRequestEvent.isCancelled()) {
            settingsRequestEvent.getSettings().forEach((id, window) -> {
                ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                re.formId = id;
                re.data = window.getJSONData();
                playerHandle.player.sendPacket(re);
            });
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET);
    }
}
