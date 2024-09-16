package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetPlayerGameTypePacket;
import org.jetbrains.annotations.NotNull;

public class SetPlayerGameTypeProcessor extends DataPacketProcessor<SetPlayerGameTypePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetPlayerGameTypePacket pk) {
        if (pk.gamemode != playerHandle.player.gamemode && playerHandle.player.hasPermission("nukkit.command.gamemode")) {
            playerHandle.player.setGamemode(switch (pk.gamemode) {
                case 0, 1, 2 -> pk.gamemode;
                case 6 -> 3;
                case 5 -> playerHandle.player.getServer().getDefaultGamemode();
                default -> throw new IllegalStateException("Unexpected value: " + pk.gamemode);
            });
            Command.broadcastCommandMessage(playerHandle.player, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(playerHandle.player.gamemode, true)));
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;
    }
}
