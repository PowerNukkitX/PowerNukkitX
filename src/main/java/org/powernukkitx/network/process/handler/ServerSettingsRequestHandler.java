package org.powernukkitx.network.process.handler;

import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerServerSettingsRequestEvent;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
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