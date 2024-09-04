package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.inventory.FullContainerName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InventoryContentPacket extends DataPacket {

    public int inventoryId;
    public Item[] slots = Item.EMPTY_ARRAY;
    public FullContainerName fullContainerName;
    public int dynamicContainerSize;

    @Override
    public int pid() {
        return ProtocolInfo.INVENTORY_CONTENT_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.inventoryId);
        byteBuf.writeUnsignedVarInt(this.slots.length);
        for (Item slot : this.slots) {
            byteBuf.writeSlot(slot);
        }
        byteBuf.writeFullContainerName(this.fullContainerName);
        byteBuf.writeUnsignedVarInt(this.dynamicContainerSize);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
