package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStartItemCoolDownPacket extends DataPacket {
    private String itemCategory;
    private int coolDownDuration;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.PLAYER_START_ITEM_COOL_DOWN_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.itemCategory = byteBuf.readString();
        this.coolDownDuration = byteBuf.readVarInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(itemCategory);
        byteBuf.writeVarInt(coolDownDuration);
    }
    /**
     * @deprecated 
     */
    

    public String getItemCategory() {
        return itemCategory;
    }
    /**
     * @deprecated 
     */
    

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }
    /**
     * @deprecated 
     */
    

    public int getCoolDownDuration() {
        return coolDownDuration;
    }
    /**
     * @deprecated 
     */
    

    public void setCoolDownDuration(int coolDownDuration) {
        this.coolDownDuration = coolDownDuration;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
