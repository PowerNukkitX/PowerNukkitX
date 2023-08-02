package cn.nukkit.network.process.processor;

import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class SetLocalPlayerAsInitializedProcessor extends DataPacketProcessor<SetLocalPlayerAsInitializedPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetLocalPlayerAsInitializedPacket pk) {
        Player player = playerHandle.getPlayer();
        if (player.isLocallyInitialized()) {
            return;
        }
        player.getPlayerConnection().setLocallyInitialized(true);
        playerHandle.handlePlayerLocallyInitialized();
        PlayerLocallyInitializedEvent event = new PlayerLocallyInitializedEvent(player);
        event.call();
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET);
    }
}
