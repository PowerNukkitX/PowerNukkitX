package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.CommandRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class CommandRequestProcessor extends DataPacketProcessor<CommandRequestPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull CommandRequestPacket pk) {
        if (!playerHandle.packetRateLimiter.tryCommand()) {
            PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(
                    playerHandle.player, PlayerHackDetectedEvent.HackType.COMMAND_SPAM);
            playerHandle.player.getServer().getPluginManager().callEvent(event);
            if (event.isKick()) {
                playerHandle.player.getSession().close("Exceeding command spam rate-limit");
            }
            return;
        }
        if (!playerHandle.player.spawned || !playerHandle.player.isAlive()) {
            return;
        }
        PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(playerHandle.player, pk.command);
        playerHandle.player.getServer().getPluginManager().callEvent(playerCommandPreprocessEvent);
        if (playerCommandPreprocessEvent.isCancelled()) {
            return;
        }
        playerHandle.player.getServer().executeCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage());
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.COMMAND_REQUEST_PACKET;
    }
}
