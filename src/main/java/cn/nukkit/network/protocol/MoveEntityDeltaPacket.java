package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import lombok.ToString;

@ToString
public class MoveEntityDeltaPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int FLAG_HAS_X = 0B1;
    public static final int FLAG_HAS_Y = 0B10;
    public static final int FLAG_HAS_Z = 0B100;
    public static final int FLAG_HAS_PITCH = 0B1000;
    public static final int FLAG_HAS_YAW = 0B10000;
    public static final int FLAG_HAS_HEAD_YAW = 0B100000;
    @Since("FUTURE") public static final int FLAG_ON_GROUND = 0B1000000;
    @Since("FUTURE") public static final int FLAG_TELEPORTING = 0B10000000;
    @Since("FUTURE") public static final int FLAG_FORCE_MOVE_LOCAL_ENTITY = 0B100000000;

    @Since("FUTURE") public long runtimeEntityId;
    public int flags = 0;
    @Since("1.4.0.0-PN") public float x = 0;
    @Since("1.4.0.0-PN") public float y = 0;
    @Since("1.4.0.0-PN") public float z = 0;
    @Since("FUTURE") public float pitch = 0;
    @Since("FUTURE") public float yaw = 0;
    @Since("FUTURE") public float headYaw = 0;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.runtimeEntityId = this.getEntityRuntimeId();
        this.flags = this.getLShort();
        if ((this.flags & FLAG_HAS_X) != 0) {
            this.x = this.getCoordinate();
        }
        if ((this.flags & FLAG_HAS_Y) != 0) {
            this.y = this.getCoordinate();
        }
        if ((this.flags & FLAG_HAS_Z) != 0) {
            this.z = this.getCoordinate();
        }
        if ((this.flags & FLAG_HAS_PITCH) != 0) {
            this.pitch = this.getRotation();
        }
        if ((this.flags & FLAG_HAS_YAW) != 0) {
            this.yaw = this.getRotation();
        }
        if ((this.flags & FLAG_HAS_HEAD_YAW) != 0) {
            this.headYaw = this.getRotation();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.runtimeEntityId);
        this.putLShort(this.flags);
        if ((this.flags & FLAG_HAS_X) != 0) {
            this.putCoordinate(this.x);
        }
        if ((this.flags & FLAG_HAS_Y) != 0) {
            this.putCoordinate(this.y);
        }
        if ((this.flags & FLAG_HAS_Z) != 0) {
            this.putCoordinate(this.z);
        }
        if ((this.flags & FLAG_HAS_PITCH) != 0) {
            this.putRotation(this.pitch);
        }
        if ((this.flags & FLAG_HAS_YAW) != 0) {
            this.putRotation(this.yaw);
        }
        if ((this.flags & FLAG_HAS_HEAD_YAW) != 0) {
            this.putRotation(this.headYaw);
        }
    }

    private float getCoordinate() {
        return this.getLFloat();
    }

    private void putCoordinate(float value) {
        this.putLFloat(value);
    }

    private float getRotation() {
        return this.getByte() * (360F / 256F);
    }

    private void putRotation(float value) {
        this.putByte((byte) (value / (360F / 256F)));
    }

    @Since("FUTURE")
    public boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
    }
}
