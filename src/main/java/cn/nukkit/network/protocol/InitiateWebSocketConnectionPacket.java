package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class InitiateWebSocketConnectionPacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.INITIATE_WEB_SOCKET_CONNECTION_PACKET;
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
