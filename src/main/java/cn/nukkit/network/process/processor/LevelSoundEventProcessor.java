package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class LevelSoundEventProcessor extends DataPacketProcessor<LevelSoundEventPacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull LevelSoundEventPacket pk) {
        Player $1 = playerHandle.player;
        if (!player.isSpectator()) {
            player.level.addChunkPacket(player.getChunkX(), player.getChunkZ(), pk);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.LEVEL_SOUND_EVENT_PACKET;
    }
}
