package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransferPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.TRANSFER_PACKET;

    public String address; // Server address
    public int $2 = 19132; // Server port

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.address = byteBuf.readString();
        this.port = (short) byteBuf.readShortLE();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(address);
        byteBuf.writeShortLE(port);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.TRANSFER_PACKET;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
