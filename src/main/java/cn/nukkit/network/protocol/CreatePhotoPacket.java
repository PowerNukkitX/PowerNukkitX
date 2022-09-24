package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

@Since("1.19.30-r1")
@PowerNukkitXOnly
@ToString
public class CreatePhotoPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CREATE_PHOTO_PACKET;
    public long id;
    public String photoName;
    public String photoItemName;


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
        this.putLLong(id);
        this.putString(photoName);
        this.putString(photoItemName);
    }
}
