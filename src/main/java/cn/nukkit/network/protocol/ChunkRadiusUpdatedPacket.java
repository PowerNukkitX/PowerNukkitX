package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChunkRadiusUpdatedPacket extends DataPacket {
    public int radius;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.radius = byteBuf.readVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.radius);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
