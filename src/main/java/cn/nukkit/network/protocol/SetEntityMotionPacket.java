package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetEntityMotionPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SET_ENTITY_MOTION_PACKET;

    public long eid;
    public float motionX;
    public float motionY;
    public float motionZ;
    public long tick;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeVector3f(this.motionX, this.motionY, this.motionZ);
        byteBuf.writeUnsignedVarLong(this.tick);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
