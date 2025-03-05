package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerPostMovePositionPacket extends DataPacket {
    public Vector3f position;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.position = byteBuf.readVector3f();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVector3f(this.position);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVER_POST_MOVE_POSITION;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
