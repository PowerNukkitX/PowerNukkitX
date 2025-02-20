package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestChunkRadiusPacket extends DataPacket {
    public int radius;

    /**
     * @since v582
     */
    private int maxRadius;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.radius = byteBuf.readVarInt();
        this.maxRadius = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
