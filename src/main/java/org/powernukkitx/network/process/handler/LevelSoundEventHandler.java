package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket;

/**
 * @author Kaooot
 */
public class LevelSoundEventHandler implements PacketHandler<LevelSoundEventPacket> {

    @Override
    public void handle(LevelSoundEventPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (!player.isSpectator()) {
            player.level.addChunkPacket(player.getChunkX(), player.getChunkZ(), packet);
        }
    }
}