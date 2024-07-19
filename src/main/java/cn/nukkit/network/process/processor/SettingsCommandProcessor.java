package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SettingsCommandPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SettingsCommandProcessor extends DataPacketProcessor<SettingsCommandPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SettingsCommandPacket pk) {
        Player player = playerHandle.player.asPlayer();

        String command = pk.command.toLowerCase(Locale.ENGLISH);
        player.getServer().executeCommand(player, command);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SETTINGS_COMMAND_PACKET;
    }
}
