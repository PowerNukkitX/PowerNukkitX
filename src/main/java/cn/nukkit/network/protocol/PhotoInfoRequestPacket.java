package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

@Since("1.19.30-r1")
@PowerNukkitXOnly
@ToString
public class PhotoInfoRequestPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.PHOTO_INFO_REQUEST_PACKET;
    public long photoId;


    @Override
    public byte pid() {
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
}
