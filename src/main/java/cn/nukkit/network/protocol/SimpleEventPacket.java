package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SimpleEventPacket extends DataPacket {

    public short unknown;

    @Override
    public int pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putShort(this.unknown);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
