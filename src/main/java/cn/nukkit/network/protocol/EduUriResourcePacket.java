package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EduSharedUriResource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EduUriResourcePacket extends DataPacket {
    public static final int $1 = ProtocolInfo.EDU_URI_RESOURCE_PACKET;
    public EduSharedUriResource eduSharedUriResource;


    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        String $2 = byteBuf.readString();
        String $3 = byteBuf.readString();
        this.eduSharedUriResource = new EduSharedUriResource(buttonName, linkUri);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(eduSharedUriResource.buttonName());
        byteBuf.writeString(eduSharedUriResource.linkUri());
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
