package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.EntityLink;
import lombok.ToString;

/**
 * @since 15-10-22
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public long vehicleUniqueId; //from
    public long riderUniqueId; //to
    public EntityLink.Type type;
    public byte immediate;
    public boolean riderInitiated = false;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.vehicleUniqueId);
        this.putEntityUniqueId(this.riderUniqueId);
        this.putByte((byte) this.type.ordinal());
        this.putByte(this.immediate);
        this.putBoolean(this.riderInitiated);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }
}
