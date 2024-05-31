package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RespawnPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.RESPAWN_PACKET;

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;

    public float x;
    public float y;
    public float z;
    public int $5 = STATE_SEARCHING_FOR_SPAWN;
    public long runtimeEntityId;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        Vector3f $6 = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.respawnState = byteBuf.readByte();
        this.runtimeEntityId = byteBuf.readEntityRuntimeId();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeByte((byte) respawnState);
        byteBuf.writeEntityRuntimeId(runtimeEntityId);
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
