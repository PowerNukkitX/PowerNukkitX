package cn.nukkit.network.process;

import cn.nukkit.PlayerHandle;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * DataPacketManager is a static class to manage DataPacketProcessors and process DataPackets.
 */
@Since("1.19.80-r2")
@PowerNukkitXOnly
public final class DataPacketManager {
    private static final Int2ObjectOpenHashMap<DataPacketProcessor> CURRENT_PROTOCOL_PROCESSORS = new Int2ObjectOpenHashMap<>(300);

    public static void registerProcessor(@NotNull DataPacketProcessor @NotNull ... processors) {
        for (var processor : processors) {
            if (processor.getProtocol() != ProtocolInfo.CURRENT_PROTOCOL) {
                throw new IllegalArgumentException("Processor protocol " + processor.getProtocol() + " does not match current protocol " + ProtocolInfo.CURRENT_PROTOCOL
                        + ". Multi-version support is not implemented yet.");
            }
            CURRENT_PROTOCOL_PROCESSORS.put(processor.getPacketId(), processor);
        }
        CURRENT_PROTOCOL_PROCESSORS.trim();
    }

    public static void processPacket(@NotNull PlayerHandle playerHandle, @NotNull DataPacket packet) {
        var processor = CURRENT_PROTOCOL_PROCESSORS.get(packet.packetId());
        if (processor != null) {
            processor.handle(playerHandle, packet);
        } else {
            throw new UnsupportedOperationException("No processor found for packet " + packet.getClass().getName() + " with id " + packet.packetId() + ".");
        }
    }
}
