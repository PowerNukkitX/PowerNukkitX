package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
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
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarLong(this.eid);
        byteBuf.writeVarInt(this.eventData.getType().ordinal());
        byteBuf.writeByte(this.usePlayerId);
        eventData.write(byteBuf);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
