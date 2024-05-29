package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.CommandRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import com.google.common.util.concurrent.RateLimiter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CommandRequestProcessor extends DataPacketProcessor<CommandRequestPacket> {
    final RateLimiter rateLimiter = RateLimiter.create(500);

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull CommandRequestPacket pk) {
        int length = pk.command.length();
        if (!rateLimiter.tryAcquire(length, 300, TimeUnit.MILLISECONDS)) {
            playerHandle.player.getSession().close("kick because hack");
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
