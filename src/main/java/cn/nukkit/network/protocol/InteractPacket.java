package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 15-10-15
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InteractPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.INTERACT_PACKET;

    public static final int $2 = 3;
    public static final int $3 = 4;

    public static final int $4 = 6;

    public int action;
    public long target;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.action = byteBuf.readByte();
        this.target = byteBuf.readEntityRuntimeId();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte((byte) this.action);
        byteBuf.writeEntityRuntimeId(this.target);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
