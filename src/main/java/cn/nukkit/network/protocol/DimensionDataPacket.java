package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class DimensionDataPacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.DIMENSION_DATA_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
