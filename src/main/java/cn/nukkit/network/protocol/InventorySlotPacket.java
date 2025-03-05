package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.inventory.FullContainerName;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;
import org.apache.logging.log4j.core.net.Protocol;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InventorySlotPacket extends DataPacket {
    public int inventoryId;
    public int slot;
    public FullContainerName fullContainerName = new FullContainerName(ContainerSlotType.ANVIL_INPUT, null);
    public Item storageItem = Item.AIR; // is air if the item is not a bundle
    public Item item;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.inventoryId = byteBuf.readUnsignedVarInt();
        this.slot = byteBuf.readUnsignedVarInt();
        this.fullContainerName = byteBuf.readFullContainerName();
        this.storageItem = byteBuf.readSlot();
        this.item = byteBuf.readSlot();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.inventoryId);
        byteBuf.writeUnsignedVarInt(this.slot);
        byteBuf.writeFullContainerName(this.fullContainerName);
        byteBuf.writeSlot(this.storageItem);
        byteBuf.writeSlot(this.item);
    }

    @Override
    public int pid() {
        return ProtocolInfo.INVENTORY_SLOT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
