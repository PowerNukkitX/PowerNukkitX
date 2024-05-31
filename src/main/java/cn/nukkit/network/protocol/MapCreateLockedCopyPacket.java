package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MapCreateLockedCopyPacket extends DataPacket {

    public long originalMapId;
    public long newMapId;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.originalMapId = byteBuf.readVarLong();
        this.newMapId = byteBuf.readVarLong();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarLong(this.originalMapId);
        byteBuf.writeVarLong(this.newMapId);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
