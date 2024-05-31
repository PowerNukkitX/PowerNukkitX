package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author CreeperFace
 * @since 5.3.2017
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        mapId = byteBuf.readEntityUniqueId();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
