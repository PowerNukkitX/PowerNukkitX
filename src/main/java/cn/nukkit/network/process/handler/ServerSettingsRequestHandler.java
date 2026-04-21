package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerServerSettingsRequestEvent;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerSettingsResponsePacket;

import java.util.HashMap;

/**
 * @author Kaooot
 */
public class ServerSettingsRequestHandler implements PacketHandler<ServerSettingsRequestPacket> {

    @Override
    public void handle(ServerSettingsRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        final PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(playerHandle.player, new HashMap<>(playerHandle.getServerSettings()));
        server.getPluginManager().callEvent(settingsRequestEvent);
        if (!settingsRequestEvent.isCancelled()) {
            settingsRequestEvent.getSettings().forEach((id, window) -> {
                final ServerSettingsResponsePacket responsePacket = new ServerSettingsResponsePacket();
                responsePacket.setFormID(id);
                responsePacket.setFormData(window.toJson());

                holder.getPlayer().sendPacket(responsePacket);
            });
        }
    }
}