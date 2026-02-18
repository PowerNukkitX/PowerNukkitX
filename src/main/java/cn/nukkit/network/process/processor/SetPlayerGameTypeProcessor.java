package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.SetPlayerGameTypePacket;
import org.jetbrains.annotations.NotNull;

public class SetPlayerGameTypeProcessor extends DataPacketProcessor<SetPlayerGameTypePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetPlayerGameTypePacket pk) {
        int gameMode = pk.getGamemode();
        if (gameMode != playerHandle.player.gamemode && playerHandle.player.hasPermission("nukkit.command.gamemode")) {
            playerHandle.player.setGamemode(switch (gameMode) {
                case 0, 1, 2 -> gameMode;
                case 6 -> 3;
                case 5 -> playerHandle.player.getServer().getDefaultGamemode();
                default -> throw new IllegalStateException("Unexpected value: " + gameMode);
            });
            Command.broadcastCommandMessage(playerHandle.player, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(playerHandle.player.gamemode)));
        }
    }

    @Override
    public Class<SetPlayerGameTypePacket> getPacketClass() {
        return SetPlayerGameTypePacket.class;
    }
}
