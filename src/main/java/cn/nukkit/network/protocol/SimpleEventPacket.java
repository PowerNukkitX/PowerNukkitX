package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SimpleEventPacket extends DataPacket {
    public short type;

    @Override
    public int pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeShort(this.type);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
