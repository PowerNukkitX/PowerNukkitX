package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.SettingsCommandPacket;

import java.util.Locale;

/**
 * @author Kaooot
 */
@Slf4j
public class SettingsCommandHandler implements PacketHandler<SettingsCommandPacket> {
    private static final int MAX_COMMAND_LENGTH = 512;

    @Override
    public void handle(SettingsCommandPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();

        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (!holder.getPlayerHandle().packetRateLimiter.tryChat()) {
            return;
        }

        String rawCommand = packet.getCommand();

        if (rawCommand == null || rawCommand.isEmpty()) {
            return;
        }

        if (rawCommand.length() > MAX_COMMAND_LENGTH) {
            log.warn("{} sent an oversized SettingsCommand ({} chars)", player.getName(), rawCommand.length());
            player.close("§cPacket handling error");
            return;
        }

        int breakLine = rawCommand.indexOf('\n');
        if (breakLine != -1) {
            rawCommand = rawCommand.substring(0, breakLine);
        }

        if (rawCommand.isEmpty()) {
            return;
        }

        String[] parts = rawCommand.split(" ", 2);
        String command = parts[0].toLowerCase(Locale.ENGLISH) + (parts.length > 1 ? " " + parts[1] : "");

        player.getServer().executeCommand(player, command);
    }
}
