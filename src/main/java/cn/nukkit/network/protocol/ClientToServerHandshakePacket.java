package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ClientToServerHandshakePacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET;
    }

    @Override
    public void decode() {
        //no content
    }

    @Override
    public void encode() {

    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
