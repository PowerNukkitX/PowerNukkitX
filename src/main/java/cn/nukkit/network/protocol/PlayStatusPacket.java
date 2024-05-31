package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 15-10-13
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayStatusPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.PLAY_STATUS_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    public static final int $5 = 3;
    public static final int $6 = 4;
    public static final int $7 = 5;
    public static final int $8 = 6;
    public static final int $9 = 7;
    public static final int $10 = 8;
    public static final int $11 = 9;

    public int status;

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

        byteBuf.writeInt(this.status);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
