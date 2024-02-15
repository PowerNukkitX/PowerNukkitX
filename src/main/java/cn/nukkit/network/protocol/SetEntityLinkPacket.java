package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.EntityLink;
import lombok.ToString;

/**
 * @since 15-10-22
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {
    public static final class Buildeur {
        public static SetEntityLinkPacket rider(long vehicleUniqueId, long riderUniqueId) {
            return rider(vehicleUniqueId, riderUniqueId, false);
        }

        public static SetEntityLinkPacket rider(long vehicleUniqueId, long riderUniqueId, boolean riderInitiated) {
            return build(vehicleUniqueId, riderUniqueId, EntityLink.Type.RIDER, (byte) 1, riderInitiated);
        }

        public static SetEntityLinkPacket build(long vehicleUniqueId, long riderUniqueId, EntityLink.Type type, boolean riderInitiated) {
            return build(vehicleUniqueId, riderUniqueId, type, (byte) 0, riderInitiated);
        }

        public static SetEntityLinkPacket build(long vehicleUniqueId, long riderUniqueId, EntityLink.Type type, byte immediate, boolean riderInitiated) {
            return new SetEntityLinkPacket(vehicleUniqueId, riderUniqueId, type, immediate, riderInitiated);
        }
    }

    public static final int NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    private long vehicleUniqueId; //from
    private long riderUniqueId; //to
    private EntityLink.Type type;
    private byte immediate;
    private boolean riderInitiated = false;

    public SetEntityLinkPacket() {

    }

    private SetEntityLinkPacket(long vehicleUniqueId, long riderUniqueId, EntityLink.Type type, byte immediate, boolean riderInitiated) {
        this.vehicleUniqueId = vehicleUniqueId; //from
        this.riderUniqueId = riderUniqueId; //to
        this.type = type;
        this.immediate = immediate;
        this.riderInitiated = riderInitiated;
    }

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
