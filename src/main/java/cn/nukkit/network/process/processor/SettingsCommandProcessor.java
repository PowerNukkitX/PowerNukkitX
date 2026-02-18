package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.SettingsCommandPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SettingsCommandProcessor extends DataPacketProcessor<SettingsCommandPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SettingsCommandPacket pk) {
        Player player = playerHandle.player.asPlayer();

        String command = pk.getCommand().toLowerCase(Locale.ENGLISH);
        player.getServer().executeCommand(player, command);
    }
    @Override
    public Class<SettingsCommandPacket> getPacketClass() {
        return SettingsCommandPacket.class;
    }
}
