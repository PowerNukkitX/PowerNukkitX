package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetLocalPlayerAsInitializedPacket extends DataPacket {
    public long eid;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        eid = byteBuf.readUnsignedVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarLong(eid);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
