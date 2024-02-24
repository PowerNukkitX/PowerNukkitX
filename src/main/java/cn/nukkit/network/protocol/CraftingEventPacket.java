package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class CraftingEventPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;


    public static final int TYPE_INVENTORY = 0;


    public static final int TYPE_CRAFTING = 1;


    public static final int TYPE_WORKBENCH = 2;

    public int windowId;
    public int type;
    public UUID id;

    public Item[] input;
    public Item[] output;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.type = byteBuf.readVarInt();
        this.id = byteBuf.readUUID();

        this.input = byteBuf.readArray(Item.class, HandleByteBuf::readSlot);
        this.output = byteBuf.readArray(Item.class, HandleByteBuf::readSlot);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) (windowId & 0xFF));
        byteBuf.writeVarInt(type);
        byteBuf.writeUUID(id);

        byteBuf.writeArray(input, byteBuf::writeSlot);
        byteBuf.writeArray(output, byteBuf::writeSlot);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
