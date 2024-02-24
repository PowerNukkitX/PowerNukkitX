package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class RespawnPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESPAWN_PACKET;

    public static final int STATE_SEARCHING_FOR_SPAWN = 0;
    public static final int STATE_READY_TO_SPAWN = 1;
    public static final int STATE_CLIENT_READY_TO_SPAWN = 2;

    public float x;
    public float y;
    public float z;
    public int respawnState = STATE_SEARCHING_FOR_SPAWN;
    public long runtimeEntityId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        Vector3f v = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.respawnState = byteBuf.readByte();
        this.runtimeEntityId = byteBuf.readEntityRuntimeId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeByte((byte) respawnState);
        byteBuf.writeEntityRuntimeId(runtimeEntityId);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
