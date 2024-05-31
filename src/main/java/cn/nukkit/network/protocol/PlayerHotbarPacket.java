package cn.nukkit.network.protocol;

import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHotbarPacket extends DataPacket {

    public int selectedHotbarSlot;
    public int $1 = SpecialWindowId.PLAYER.getId();

    public boolean $2 = true;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.PLAYER_HOTBAR_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.selectedHotbarSlot = (int) byteBuf.readUnsignedVarInt();
        this.windowId = byteBuf.readByte();
        this.selectHotbarSlot = byteBuf.readBoolean();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(this.selectedHotbarSlot);
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeBoolean(this.selectHotbarSlot);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
