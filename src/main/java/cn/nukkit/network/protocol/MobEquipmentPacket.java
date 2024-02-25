package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class MobEquipmentPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.MOB_EQUIPMENT_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public long eid;
    public Item item;
    public int inventorySlot;
    public int hotbarSlot;
    public int windowId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId(); //EntityRuntimeID
        this.item = byteBuf.readSlot();
        this.inventorySlot = byteBuf.readByte();
        this.hotbarSlot = byteBuf.readByte();
        this.windowId = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.eid); //EntityRuntimeID
        byteBuf.writeSlot(this.item);
        byteBuf.writeByte((byte) this.inventorySlot);
        byteBuf.writeByte((byte) this.hotbarSlot);
        byteBuf.writeByte((byte) this.windowId);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
