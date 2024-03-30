package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientCacheStatusPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CLIENT_CACHE_STATUS_PACKET;

    public boolean supported;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.supported = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeBoolean(this.supported);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
