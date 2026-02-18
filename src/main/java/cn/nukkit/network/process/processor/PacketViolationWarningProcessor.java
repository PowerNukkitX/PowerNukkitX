package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.PacketViolationWarningPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class PacketViolationWarningProcessor extends DataPacketProcessor<PacketViolationWarningPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PacketViolationWarningPacket pk) {
        log.warn("Violation warning from {}: {}", playerHandle.player.getName(), pk);
    }

    @Override
    public Class<PacketViolationWarningPacket> getPacketClass() {
        return PacketViolationWarningPacket.class;
    }
}
