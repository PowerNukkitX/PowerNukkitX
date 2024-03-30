package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhotoInfoRequestPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.PHOTO_INFO_REQUEST_PACKET;
    public long photoId;


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityUniqueId(photoId);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
