package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.SetDifficultyPacket;
import org.jetbrains.annotations.NotNull;

public class SetDifficultyProcessor extends DataPacketProcessor<SetDifficultyPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetDifficultyPacket pk) {
        if (!playerHandle.player.spawned || !playerHandle.player.hasPermission("nukkit.command.difficulty")) {
            return;
        }
        playerHandle.player.getServer().setDifficulty(pk.getDifficulty());
        SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
        difficultyPacket.setDifficulty(playerHandle.player.getServer().getDifficulty());
        for (var onlinePlayer : playerHandle.player.getServer().getOnlinePlayers().values()) {
            onlinePlayer.dataPacket(difficultyPacket);
        }
        Command.broadcastCommandMessage(playerHandle.player, new TranslationContainer("commands.difficulty.success", String.valueOf(playerHandle.player.getServer().getDifficulty())));
    }
    @Override
    public Class<SetDifficultyPacket> getPacketClass() {
        return SetDifficultyPacket.class;
    }
}
