package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MapCreateLockedCopyPacket extends DataPacket {
    public long originalMapId;
    public long newMapId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.originalMapId = byteBuf.readVarLong();
        this.newMapId = byteBuf.readVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarLong(this.originalMapId);
        byteBuf.writeVarLong(this.newMapId);
    }

    @Override
    public int pid() {
        return ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
