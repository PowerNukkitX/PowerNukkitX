package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class SetLocalPlayerAsInitializedPacketProcessor extends DataPacketProcessor<SetLocalPlayerAsInitializedPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetLocalPlayerAsInitializedPacket pk) {
        Player player = playerHandle.player;
        log.debug("receive SetLocalPlayerAsInitializedPacket for {}", player.getPlayerInfo().getUsername());
        playerHandle.onPlayerLocallyInitialized();
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;
    }
}
