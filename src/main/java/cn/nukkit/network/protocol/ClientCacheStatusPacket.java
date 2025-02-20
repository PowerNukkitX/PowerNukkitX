package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientCacheStatusPacket extends DataPacket {
    public boolean supported;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.supported = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.supported);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENT_CACHE_STATUS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
