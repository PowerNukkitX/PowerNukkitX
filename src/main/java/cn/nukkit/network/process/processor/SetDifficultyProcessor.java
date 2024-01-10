package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetDifficultyPacket;
import org.jetbrains.annotations.NotNull;

public class SetDifficultyProcessor extends DataPacketProcessor<SetDifficultyPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetDifficultyPacket pk) {
        if (!playerHandle.player.spawned || !playerHandle.player.hasPermission("nukkit.command.difficulty")) {
            return;
        }
        playerHandle.player.getServer().setDifficulty(pk.difficulty);
        SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
        difficultyPacket.difficulty = playerHandle.player.getServer().getDifficulty();
        Server.broadcastPacket(playerHandle.player.getServer().getOnlinePlayers().values(), difficultyPacket);
        Command.broadcastCommandMessage(playerHandle.player, new TranslationContainer("commands.difficulty.success", String.valueOf(playerHandle.player.getServer().getDifficulty())));
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SET_DIFFICULTY_PACKET;
    }
}
