package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class RemoveEntityPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.REMOVE_ENTITY_PACKET;

    public long eid;

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
        this.putEntityUniqueId(this.eid);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
