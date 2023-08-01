package cn.nukkit.network.process.processor;

import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class SetLocalPlayerAsInitializedProcessor extends DataPacketProcessor<SetLocalPlayerAsInitializedPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetLocalPlayerAsInitializedPacket pk) {
        if (playerHandle.player.isLocallyInitialized()) {
            return;
        }
        playerHandle.player.getPlayerConnection().setLocallyInitialized(true);
        playerHandle.onPlayerLocallyInitialized();
        PlayerLocallyInitializedEvent locallyInitializedEvent = new PlayerLocallyInitializedEvent(playerHandle.player);
        locallyInitializedEvent.call();
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET);
    }
}
