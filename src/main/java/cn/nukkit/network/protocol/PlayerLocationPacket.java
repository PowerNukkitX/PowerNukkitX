package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerLocationPacket extends DataPacket {

    private Type type;
    private long targetEntityId;
    private Vector3f position;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.type = Type.values()[byteBuf.readIntLE()];
        this.targetEntityId = byteBuf.readEntityRuntimeId();
        if(this.type == Type.COORDINATES) {
            this.position = byteBuf.readVector3f();
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(type.ordinal());
        byteBuf.writeLong(targetEntityId);
        if (getType() == PlayerLocationPacket.Type.COORDINATES) {
            byteBuf.writeVector3f(position);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_LOCATION_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public enum Type {
        COORDINATES,
        HIDE
    }

}
