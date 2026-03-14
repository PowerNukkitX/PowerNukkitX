package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SetLastHurtByPacket extends DataPacket {
    public int entityTypeId;


    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarLong(this.entityTypeId);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_LAST_HURT_BY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
