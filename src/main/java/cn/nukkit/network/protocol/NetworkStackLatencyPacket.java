package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NetworkStackLatencyPacket extends DataPacket {
    public long timestamp;
    public boolean unknownBool;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        timestamp = byteBuf.readLongLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLongLE(timestamp);
        byteBuf.writeBoolean(unknownBool);
    }

    @Override
    public int pid() {
        return ProtocolInfo.NETWORK_STACK_LATENCY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
