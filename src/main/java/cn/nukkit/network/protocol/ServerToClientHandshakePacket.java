package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerToClientHandshakePacket extends DataPacket {

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.SERVER_TO_CLIENT_HANDSHAKE_PACKET;
    }

    public String jwt;

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
        

        byteBuf.writeString(this.jwt);
    }
    /**
     * @deprecated 
     */
    

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    /**
     * @deprecated 
     */
    

    public String getJwt() {
        return jwt;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
