package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TickingAreasLoadStatusPacket extends DataPacket {
    boolean waitingForPreload;

    @Override
    public int pid() {
        return ProtocolInfo.TICKING_AREAS_LOAD_STATUS_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.waitingForPreload);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
