package cn.nukkit.network.process.processor;

import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetPlayerGameTypePacket;
import cn.nukkit.player.GameMode;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class SetPlayerGameTypeProcessor extends DataPacketProcessor<SetPlayerGameTypePacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetPlayerGameTypePacket pk) {
        Player player = playerHandle.getPlayer();
        if (pk.gamemode != player.getGamemode().ordinal() && player.hasPermission("nukkit.command.gamemode")) {
            player.setGamemode(GameMode.fromOrdinal(
                    switch (pk.gamemode) {
                        case 0, 1, 2 -> pk.gamemode;
                        case 6 -> 3;
                        case 5 -> player.getServer().getGamemode().ordinal();
                        default -> throw new IllegalStateException("Unexpected value: " + pk.gamemode);
                    }));
            Command.broadcastCommandMessage(
                    player,
                    new TranslationContainer(
                            "commands.gamemode.success.self",
                            player.getGamemode().getTranslatableName()));
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET);
    }
}
