package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntityFallPacket extends DataPacket {
    public long eid;
    public float fallDistance;
    public boolean unknown;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId();
        this.fallDistance = byteBuf.readFloatLE();
        this.unknown = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.ENTITY_FALL_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
