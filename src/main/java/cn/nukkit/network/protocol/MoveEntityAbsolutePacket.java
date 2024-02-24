package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class MoveEntityAbsolutePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET;
    public static final byte FLAG_GROUND = 0x01;
    public static final byte FLAG_TELEPORT = 0x02;
    public static final byte FLAG_FORCE_MOVE_LOCAL_ENTITY = 0x04;

    public long eid;
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double headYaw;
    public double pitch;
    public boolean onGround;
    public boolean teleport;
    public boolean forceMoveLocalEntity;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId();
        int flags = byteBuf.readByte();
        onGround = (flags & 0x01) != 0;
        teleport = (flags & 0x02) != 0;
        forceMoveLocalEntity = (flags & 0x04) != 0;
        Vector3f v = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = byteBuf.readByte() * (360d / 256d);
        this.headYaw = byteBuf.readByte() * (360d / 256d);
        this.yaw = byteBuf.readByte() * (360d / 256d);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeEntityRuntimeId(this.eid);
        byte flags = 0;
        if (onGround) {
            flags |= 0x01;
        }
        if (teleport) {
            flags |= 0x02;
        }
        if (forceMoveLocalEntity) {
            flags |= 0x04;
        }
        byteBuf.writeByte(flags);
        byteBuf.writeVector3f((float) this.x, (float) this.y, (float) this.z);
        byteBuf.writeByte((byte) (this.pitch / (360d / 256d)));
        byteBuf.writeByte((byte) (this.headYaw / (360d / 256d)));
        byteBuf.writeByte((byte) (this.yaw / (360d / 256d)));
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
