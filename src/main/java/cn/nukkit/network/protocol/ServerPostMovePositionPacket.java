package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerPostMovePositionPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SERVER_POST_MOVE_POSITION;

    public Vector3f position;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.position = byteBuf.readVector3f();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVector3f(this.position);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
