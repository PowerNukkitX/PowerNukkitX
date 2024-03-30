package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MoveEntityDeltaPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int FLAG_HAS_X = 0B1;
    public static final int FLAG_HAS_Y = 0B10;
    public static final int FLAG_HAS_Z = 0B100;
    public static final int FLAG_HAS_PITCH = 0B1000;
    public static final int FLAG_HAS_YAW = 0B10000;
    public static final int FLAG_HAS_HEAD_YAW = 0B100000;
    public static final int FLAG_ON_GROUND = 0B1000000;
    public static final int FLAG_TELEPORTING = 0B10000000;
    public static final int FLAG_FORCE_MOVE_LOCAL_ENTITY = 0B100000000;

    public long runtimeEntityId;
    public int flags = 0;
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public float pitch = 0;
    public float yaw = 0;
    public float headYaw = 0;


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
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

    private float getCoordinate(HandleByteBuf byteBuf) {
        return byteBuf.readFloatLE();
    }

    private void putCoordinate(HandleByteBuf byteBuf, float value) {
        byteBuf.writeFloatLE(value);
    }

    private float getRotation(HandleByteBuf byteBuf) {
        return byteBuf.readByte() * (360F / 256F);
    }

    private void putRotation(HandleByteBuf byteBuf, float value) {
        byteBuf.writeByte((byte) (value / (360F / 256F)));
    }

    public boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
