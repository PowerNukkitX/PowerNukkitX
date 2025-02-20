package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

/**
 * @since 15-10-14
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MovePlayerPacket extends DataPacket {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_RESET = 1;//MODE_RESPAWN
    public static final int MODE_TELEPORT = 2;
    public static final int MODE_PITCH = 3; //facepalm Mojang MODE_HEAD_ROTATION

    public long eid;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float headYaw;
    public float pitch;
    public int mode = MODE_NORMAL;
    public boolean onGround;
    public long ridingEid;
    public int teleportationCause = 0;
    public int entityType = 0;

    public long frame;//tick

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId();
        Vector3f v = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = byteBuf.readFloatLE();
        this.yaw = byteBuf.readFloatLE();
        this.headYaw = byteBuf.readFloatLE();
        this.mode = byteBuf.readByte();
        this.onGround = byteBuf.readBoolean();
        this.ridingEid = byteBuf.readEntityRuntimeId();
        if (this.mode == MODE_TELEPORT) {
            this.teleportationCause = byteBuf.readIntLE();
            this.entityType = byteBuf.readIntLE();
        }
        this.frame = byteBuf.readUnsignedVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeFloatLE(this.pitch);
        byteBuf.writeFloatLE(this.yaw);
        byteBuf.writeFloatLE(this.headYaw);
        byteBuf.writeByte((byte) this.mode);
        byteBuf.writeBoolean(this.onGround);
        byteBuf.writeEntityRuntimeId(this.ridingEid);
        if (this.mode == MODE_TELEPORT) {
            byteBuf.writeIntLE(this.teleportationCause);
            byteBuf.writeIntLE(this.entityType);
        }
        byteBuf.writeUnsignedVarLong(this.frame);
    }

    @Override
    public int pid() {
        return ProtocolInfo.MOVE_PLAYER_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
