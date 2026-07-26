package org.powernukkitx.network.process.handler;

import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.command.Command;
import org.powernukkitx.lang.TranslationContainer;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.SetDifficultyPacket;

/**
 * @author Kaooot
 */
public class SetDifficultyHandler implements PacketHandler<SetDifficultyPacket> {

    @Override
    public void handle(SetDifficultyPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (!playerHandle.player.spawned || !playerHandle.player.hasPermission("nukkit.command.difficulty")) {
            return;
        }
        server.setDifficulty(packet.getDifficulty());
        final SetDifficultyPacket difficultyPacket = new SetDifficultyPacket();
        packet.setDifficulty(server.getDifficulty());
        Server.broadcastPacket(server.getOnlinePlayers().values(), difficultyPacket);
        Command.broadcastCommandMessage(playerHandle.player, new TranslationContainer("commands.difficulty.success", String.valueOf(server.getDifficulty())));
    }
}