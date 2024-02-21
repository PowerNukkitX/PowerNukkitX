package cn.nukkit.network.protocol;

import lombok.ToString;


@ToString
public class PhotoInfoRequestPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.PHOTO_INFO_REQUEST_PACKET;
    public long photoId;


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(photoId);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
