package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VideoStreamConnectPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.VIDEO_STREAM_CONNECT_PACKET;

    public static final byte $2 = 0;
    public static final byte $3 = 1;

    public String address;
    public float screenshotFrequency;
    public byte action;

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
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(address);
        byteBuf.writeFloatLE(screenshotFrequency);
        byteBuf.writeByte(action);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
