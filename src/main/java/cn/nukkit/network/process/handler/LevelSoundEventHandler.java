package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
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