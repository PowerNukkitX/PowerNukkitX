package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class RequestNetworkSettingsPacket extends DataPacket {

    public int protocolVersion;

    @Override
    public int pid() {
        return ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.protocolVersion = byteBuf.readInt();
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
