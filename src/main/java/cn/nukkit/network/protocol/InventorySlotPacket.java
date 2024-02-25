package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class InventorySlotPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.INVENTORY_SLOT_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int inventoryId;
    public int slot;
    public Item item;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.inventoryId = byteBuf.readUnsignedVarInt();
        this.slot = byteBuf.readUnsignedVarInt();
        this.item = byteBuf.readSlot();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.inventoryId);
        byteBuf.writeUnsignedVarInt(this.slot);
        byteBuf.writeSlot(this.item);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
