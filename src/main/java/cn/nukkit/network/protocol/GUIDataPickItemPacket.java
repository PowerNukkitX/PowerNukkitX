package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GUIDataPickItemPacket extends DataPacket {

    public int hotbarSlot;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeIntLE(this.hotbarSlot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.hotbarSlot = byteBuf.readIntLE();
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
