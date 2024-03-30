package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChunkRadiusUpdatedPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;

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
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
