package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RiderJumpPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.RIDER_JUMP_PACKET;

    /**
     * This is jumpStrength.
     * 对应跳跃进度条0-100
     * <p>
     * Corresponds to jump progress bars 0-100
     */
    public int unknown;

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
        this.unknown = byteBuf.readVarInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(this.unknown);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
