package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
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