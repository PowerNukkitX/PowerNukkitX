package cn.nukkit.network.process.processor;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetDifficultyPacket;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class SetDifficultyProcessor extends DataPacketProcessor<SetDifficultyPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetDifficultyPacket pk) {
        Player player = playerHandle.getPlayer();
        if (!player.isSpawned() || !player.hasPermission("nukkit.command.difficulty")) {
            return;
        }
        player.getServer().setDifficulty(pk.difficulty);
        SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
        difficultyPacket.difficulty = player.getServer().getDifficulty();
        Server.broadcastPacket(
                player.getServer().getPlayerManager().getOnlinePlayers().values(), difficultyPacket);
        Command.broadcastCommandMessage(
                player,
                new TranslationContainer(
                        "commands.difficulty.success",
                        String.valueOf(player.getServer().getDifficulty())));
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.SET_DIFFICULTY_PACKET);
    }
}
