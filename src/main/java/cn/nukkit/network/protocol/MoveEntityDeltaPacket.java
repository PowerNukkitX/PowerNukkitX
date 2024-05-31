package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MoveEntityDeltaPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int $2 = 0B1;
    public static final int $3 = 0B10;
    public static final int $4 = 0B100;
    public static final int $5 = 0B1000;
    public static final int $6 = 0B10000;
    public static final int $7 = 0B100000;
    public static final int $8 = 0B1000000;
    public static final int $9 = 0B10000000;
    public static final int $10 = 0B100000000;

    public long runtimeEntityId;
    public int $11 = 0;
    public float $12 = 0;
    public float $13 = 0;
    public float $14 = 0;
    public float $15 = 0;
    public float $16 = 0;
    public float $17 = 0;


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
        this.runtimeEntityId = byteBuf.readEntityRuntimeId();
        this.flags = byteBuf.readShortLE();
        if ((this.flags & FLAG_HAS_X) != 0) {
            this.x = this.getCoordinate(byteBuf);
        }
        if ((this.flags & FLAG_HAS_Y) != 0) {
            this.y = this.getCoordinate(byteBuf);
        }
        if ((this.flags & FLAG_HAS_Z) != 0) {
            this.z = this.getCoordinate(byteBuf);
        }
        if ((this.flags & FLAG_HAS_PITCH) != 0) {
            this.pitch = this.getRotation(byteBuf);
        }
        if ((this.flags & FLAG_HAS_YAW) != 0) {
            this.yaw = this.getRotation(byteBuf);
        }
        if ((this.flags & FLAG_HAS_HEAD_YAW) != 0) {
            this.headYaw = this.getRotation(byteBuf);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityRuntimeId(this.runtimeEntityId);
        byteBuf.writeShortLE(this.flags);
        if ((this.flags & FLAG_HAS_X) != 0) {
            this.putCoordinate(byteBuf, this.x);
        }
        if ((this.flags & FLAG_HAS_Y) != 0) {
            this.putCoordinate(byteBuf, this.y);
        }
        if ((this.flags & FLAG_HAS_Z) != 0) {
            this.putCoordinate(byteBuf, this.z);
        }
        if ((this.flags & FLAG_HAS_PITCH) != 0) {
            this.putRotation(byteBuf, this.pitch);
        }
        if ((this.flags & FLAG_HAS_YAW) != 0) {
            this.putRotation(byteBuf, this.yaw);
        }
        if ((this.flags & FLAG_HAS_HEAD_YAW) != 0) {
            this.putRotation(byteBuf, this.headYaw);
        }
    }

    
    /**
     * @deprecated 
     */
    private float getCoordinate(HandleByteBuf byteBuf) {
        return byteBuf.readFloatLE();
    }

    
    /**
     * @deprecated 
     */
    private void putCoordinate(HandleByteBuf byteBuf, float value) {
        byteBuf.writeFloatLE(value);
    }

    
    /**
     * @deprecated 
     */
    private float getRotation(HandleByteBuf byteBuf) {
        return byteBuf.readByte() * (360F / 256F);
    }

    
    /**
     * @deprecated 
     */
    private void putRotation(HandleByteBuf byteBuf, float value) {
        byteBuf.writeByte((byte) (value / (360F / 256F)));
    }
    /**
     * @deprecated 
     */
    

    public boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
