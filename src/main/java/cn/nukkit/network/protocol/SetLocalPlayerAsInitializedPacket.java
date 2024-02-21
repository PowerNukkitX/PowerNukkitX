package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetLocalPlayerAsInitializedPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;

    public long eid;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        eid = this.getUnsignedVarLong();
    }

    @Override
    public void encode() {
        this.putUnsignedVarLong(eid);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
