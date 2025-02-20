package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

/**
 * @author CreeperFace
 * @since 5.3.2017
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        mapId = byteBuf.readEntityUniqueId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
