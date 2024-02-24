package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author CreeperFace
 * @since 5.3.2017
 */
@ToString
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public int pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        mapId = byteBuf.readEntityUniqueId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
