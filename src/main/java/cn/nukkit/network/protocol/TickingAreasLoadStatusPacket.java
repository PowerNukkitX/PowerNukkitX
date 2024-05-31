package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TickingAreasLoadStatusPacket extends DataPacket {
    boolean waitingForPreload;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.TICKING_AREAS_LOAD_STATUS_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.waitingForPreload);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
