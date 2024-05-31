package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author GoodLucky777
 */


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TickSyncPacket extends DataPacket {


    public static final int $1 = ProtocolInfo.TICK_SYNC_PACKET;
    
    private long requestTimestamp;
    
    private long responseTimestamp;
    
    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.requestTimestamp = byteBuf.readLongLE();
        this.responseTimestamp = byteBuf.readLongLE();
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeLongLE(this.requestTimestamp);
        byteBuf.writeLongLE(this.responseTimestamp);
    }
    /**
     * @deprecated 
     */
    

    public long getRequestTimestamp() {
        return requestTimestamp;
    }
    /**
     * @deprecated 
     */
    

    public void setRequestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }
    /**
     * @deprecated 
     */
    

    public long getResponseTimestamp() {
        return responseTimestamp;
    }
    /**
     * @deprecated 
     */
    

    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
