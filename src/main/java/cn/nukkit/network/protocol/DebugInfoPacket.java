package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DebugInfoPacket extends DataPacket {
    public long entityId;
    public String data;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.entityId = byteBuf.readLong();
        this.data = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLong(this.entityId);
        byteBuf.writeString(this.data);
    }

    @Override
    public int pid() {
        return ProtocolInfo.DEBUG_INFO_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
