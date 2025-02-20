package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.inventory.FullContainerName;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InventoryContentPacket extends DataPacket {
    public int inventoryId;
    public Item[] slots = Item.EMPTY_ARRAY;
    public FullContainerName fullContainerName = new FullContainerName(ContainerSlotType.ANVIL_INPUT, null);
    public Item storageItem = Item.AIR; // is air if the item is not a bundle

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
        byteBuf.writeSlot(this.storageItem);
    }

    @Override
    public int pid() {
        return ProtocolInfo.INVENTORY_CONTENT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
