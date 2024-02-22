package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetHealthPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_HEALTH_PACKET;

    public int health;

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
        this.putUnsignedVarInt(this.health);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
