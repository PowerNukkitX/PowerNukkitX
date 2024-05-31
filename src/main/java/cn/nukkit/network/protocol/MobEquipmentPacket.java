package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MobEquipmentPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.MOB_EQUIPMENT_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public long eid;
    public Item item;
    public int inventorySlot;
    public int hotbarSlot;
    public int windowId;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId(); //EntityRuntimeID
        this.item = byteBuf.readSlot();
        this.inventorySlot = byteBuf.readByte();
        this.hotbarSlot = byteBuf.readByte();
        this.windowId = byteBuf.readByte();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.eid); //EntityRuntimeID
        byteBuf.writeSlot(this.item);
        byteBuf.writeByte((byte) this.inventorySlot);
        byteBuf.writeByte((byte) this.hotbarSlot);
        byteBuf.writeByte((byte) this.windowId);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
