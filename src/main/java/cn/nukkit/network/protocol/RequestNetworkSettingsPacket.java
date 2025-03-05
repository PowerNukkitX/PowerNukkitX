package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestNetworkSettingsPacket extends DataPacket {
    public int protocolVersion;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.protocolVersion = byteBuf.readInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int pid() {
        return ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
