package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

/**
 * @author GoodLucky777
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TickSyncPacket extends DataPacket {
    private long requestTimestamp;
    private long responseTimestamp;
    
    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.requestTimestamp = byteBuf.readLongLE();
        this.responseTimestamp = byteBuf.readLongLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLongLE(this.requestTimestamp);
        byteBuf.writeLongLE(this.responseTimestamp);
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public long getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    @Override
    public int pid() {
        return ProtocolInfo.TICK_SYNC_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
