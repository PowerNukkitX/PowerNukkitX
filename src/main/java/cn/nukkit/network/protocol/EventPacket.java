package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EventData;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventPacket extends DataPacket {
    public long eid;
    public boolean usePlayerId;
    public EventData eventData;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarLong(this.eid);
        byteBuf.writeVarInt(this.eventData.getType().ordinal());
        byteBuf.writeBoolean(this.usePlayerId);
        byteBuf.writeUnsignedVarInt(eventData.getType().ordinal());
        eventData.write(byteBuf);
    }

    @Override
    public int pid() {
        return ProtocolInfo.EVENT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
