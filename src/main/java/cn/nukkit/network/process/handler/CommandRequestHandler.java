package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.CommandRequestPacket;

/**
 * @author Kaooot
 */
public class CommandRequestHandler implements PacketHandler<CommandRequestPacket> {

    @Override
    public void handle(CommandRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
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
        PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(playerHandle.player, packet.getCommand());
        playerHandle.player.getServer().getPluginManager().callEvent(playerCommandPreprocessEvent);
        if (playerCommandPreprocessEvent.isCancelled()) {
            return;
        }
        playerHandle.player.getServer().executeCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage());
    }
}