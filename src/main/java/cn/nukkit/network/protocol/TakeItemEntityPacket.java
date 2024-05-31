package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 15-10-14
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TakeItemEntityPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.TAKE_ITEM_ENTITY_PACKET;

    public long entityId;
    public long target;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.target = byteBuf.readEntityRuntimeId();
        this.entityId = byteBuf.readEntityRuntimeId();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeEntityRuntimeId(this.target);
        byteBuf.writeEntityRuntimeId(this.entityId);
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
