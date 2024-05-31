package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompletedUsingItemPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.COMPLETED_USING_ITEM_PACKET;

    public static final int $2 = -1;
    public static final int $3 = 0;
    public static final int $4 = 1;
    public static final int $5 = 2;
    public static final int $6 = 3;
    public static final int $7 = 4;
    public static final int $8 = 5;
    public static final int $9 = 6;
    public static final int $10 = 7;
    public static final int $11 = 8;
    public static final int $12 = 9;
    public static final int $13 = 10;
    public static final int $14 = 11;
    public static final int $15 = 12;
    public static final int $16 = 13;
    public static final int $17 = 14;


    public static final int $18 = 15;

    public int itemId;
    public int action;


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
        
        byteBuf.writeShortLE(itemId);
        byteBuf.writeIntLE(action);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
