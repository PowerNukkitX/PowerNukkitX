package cn.nukkit.network.protocol;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author GoodLucky777
 */


@ToString
@NoArgsConstructor(onConstructor = @__())
public class TickSyncPacket extends DataPacket {


    public static final byte NETWORK_ID = ProtocolInfo.TICK_SYNC_PACKET;
    
    private long requestTimestamp;
    
    private long responseTimestamp;
    
    @Override
    public int pid() {
        return NETWORK_ID;
    }
    
    @Override
    public void decode() {
        this.requestTimestamp = this.getLLong();
        this.responseTimestamp = this.getLLong();
    }
    
    @Override
    public void encode() {
        this.reset();
        this.putLLong(this.requestTimestamp);
        this.putLLong(this.responseTimestamp);
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
}
