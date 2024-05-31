package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SpawnExperienceOrbPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET;

    public float x;
    public float y;
    public float z;
    public int amount;

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

        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeUnsignedVarInt(this.amount);
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
