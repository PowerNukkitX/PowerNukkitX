package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@Builder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientInputLocksPacket extends DataPacket {
    public int lockComponentData;
    public Vector3f serverPosition;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.lockComponentData = byteBuf.readVarInt();
        this.serverPosition = byteBuf.readVector3f();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(lockComponentData);
        byteBuf.writeVector3f(serverPosition);
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_CLIENT_INPUT_LOCKS;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
