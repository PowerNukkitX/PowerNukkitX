package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class SetLocalPlayerAsInitializedPacketProcessor extends DataPacketProcessor<SetLocalPlayerAsInitializedPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull SetLocalPlayerAsInitializedPacket pk) {
        Player player = playerHandle.player;
        log.debug("receive SetLocalPlayerAsInitializedPacket for {}", player.getName());
        playerHandle.onPlayerLocallyInitialized();
    }
    @Override
    public Class<SetLocalPlayerAsInitializedPacket> getPacketClass() {
        return SetLocalPlayerAsInitializedPacket.class;
    }
}
