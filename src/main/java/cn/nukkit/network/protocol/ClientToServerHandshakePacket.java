package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class ClientToServerHandshakePacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        //no content
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
