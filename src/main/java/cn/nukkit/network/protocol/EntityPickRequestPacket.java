package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class EntityPickRequestPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        //TODO
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
