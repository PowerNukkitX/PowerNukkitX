package cn.nukkit.network.protocol;

import lombok.ToString;


@ToString
public class CreatePhotoPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CREATE_PHOTO_PACKET;
    public long id;
    public String photoName;
    public String photoItemName;


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
        this.putLLong(id);
        this.putString(photoName);
        this.putString(photoItemName);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
