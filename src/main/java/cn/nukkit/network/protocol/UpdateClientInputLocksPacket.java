package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
public class UpdateClientInputLocksPacket extends DataPacket {
    private int lockComponentData;
    private Vector3f serverPosition;


    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_CLIENT_INPUT_LOCKS;
    }

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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
