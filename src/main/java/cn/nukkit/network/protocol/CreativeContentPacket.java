package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreativeContentPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.CREATIVE_CONTENT_PACKET;


    public Item[] entries = Item.EMPTY_ARRAY;

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

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(entries.length);
        for ($2nt $1 = 0; i < entries.length; i++) {
            byteBuf.writeUnsignedVarInt(i + 1);//netId
            byteBuf.writeSlot(entries[i], true);
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
