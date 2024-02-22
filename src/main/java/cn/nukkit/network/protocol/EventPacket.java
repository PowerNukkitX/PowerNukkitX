package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.EventData;
import lombok.ToString;

@ToString
public class EventPacket extends DataPacket {
    public long eid;
    public byte usePlayerId;
    public EventData eventData;

    @Override
    public int pid() {
        return ProtocolInfo.EVENT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(this.eid);
        this.putVarInt(this.eventData.getType().ordinal());
        this.putByte(this.usePlayerId);
        eventData.write(this);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
