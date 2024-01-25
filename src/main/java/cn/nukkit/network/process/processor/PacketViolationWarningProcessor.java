package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PacketViolationWarningPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class PacketViolationWarningProcessor extends DataPacketProcessor<PacketViolationWarningPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PacketViolationWarningPacket pk) {
        Optional<String> packetName = Arrays.stream(ProtocolInfo.class.getDeclaredFields())
                .filter(field -> field.getType() == Byte.TYPE)
                .filter(field -> {
                    try {
                        return field.getByte(null) == pk.packetId;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                }).map(Field::getName).findFirst();
        log.warn("Violation warning from {}{}", playerHandle.player.getName(), packetName.map(name -> " for packet " + name).orElse("") + ": " + pk);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET;
    }
}
