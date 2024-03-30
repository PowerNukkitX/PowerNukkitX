package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DebugInfoPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.DEBUG_INFO_PACKET;
    public long entityId;
    public String data;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
