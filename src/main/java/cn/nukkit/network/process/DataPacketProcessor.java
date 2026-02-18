package cn.nukkit.network.process;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.NotNull;

/**
 * A DataPacketProcessor is used to handle a specific type of BedrockPacket. <br/>
 * DataPacketProcessor must be <strong>thread-safe</strong>. <br/>
 * <hr/>
 * Why not interfaces? Hotspot C2 JIT cannot handle so many classes that impl the same interface, it makes the
 * performance lower.
 */
public abstract class DataPacketProcessor<T extends BedrockPacket> {
    public abstract void handle(@NotNull PlayerHandle playerHandle, @NotNull T pk);

    public abstract Class<T> getPacketClass();

    public final void handlePacket(@NotNull PlayerHandle playerHandle, @NotNull BedrockPacket pk) {
        try {
            @SuppressWarnings("unchecked")
            T typedPacket = (T) pk;
            handle(playerHandle, typedPacket);
        } catch (ClassCastException e) {
            StringBuilder message = new StringBuilder()
                    .append("DataPacketProcessor mis-registration detected. ")
                    .append("Processor=").append(getClass().getName())
                    .append(", expectedPacketClass=").append(getPacketClass().getName())
                    .append(", actualPacketClass=").append(pk.getClass().getName());
            throw new IllegalStateException(message.toString(), e);
        }
    }

    public int getProtocol() {
        return ProtocolInfo.CURRENT_PROTOCOL;
    }
}
