package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetTimePacket extends DataPacket {
    public int time;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.time);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_TIME_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
