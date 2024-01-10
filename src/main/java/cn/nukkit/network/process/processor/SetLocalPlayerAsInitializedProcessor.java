package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import org.jetbrains.annotations.NotNull;

public class SetLocalPlayerAsInitializedProcessor extends DataPacketProcessor<SetLocalPlayerAsInitializedPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetLocalPlayerAsInitializedPacket pk) {
        if (playerHandle.player.locallyInitialized) {
            return;
        }
        playerHandle.player.locallyInitialized = true;
        playerHandle.onPlayerLocallyInitialized();
        PlayerLocallyInitializedEvent locallyInitializedEvent = new PlayerLocallyInitializedEvent(playerHandle.player);
        playerHandle.player.getServer().getPluginManager().callEvent(locallyInitializedEvent);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;
    }
}
