package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientInputLocksPacket extends DataPacket {
    public int lockComponentData;
    public Vector3f serverPosition;


    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.UPDATE_CLIENT_INPUT_LOCKS;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.lockComponentData = byteBuf.readVarInt();
        this.serverPosition = byteBuf.readVector3f();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(lockComponentData);
        byteBuf.writeVector3f(serverPosition);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
