package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.SettingsCommandPacket;

import java.util.Locale;

/**
 * @author Kaooot
 */
public class SettingsCommandHandler implements PacketHandler<SettingsCommandPacket> {

    @Override
    public void handle(SettingsCommandPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        String command = packet.getCommand().toLowerCase(Locale.ENGLISH);
        player.getServer().executeCommand(player, command);
    }
}