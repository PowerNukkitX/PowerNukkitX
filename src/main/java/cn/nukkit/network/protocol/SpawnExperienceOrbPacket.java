package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SpawnExperienceOrbPacket extends DataPacket {
    public float x;
    public float y;
    public float z;
    public int amount;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeUnsignedVarInt(this.amount);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
