package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CraftingEventPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.CRAFTING_EVENT_PACKET;


    public static final int $2 = 0;


    public static final int $3 = 1;


    public static final int $4 = 2;

    public int windowId;
    public int type;
    public UUID id;

    public Item[] input;
    public Item[] output;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.type = byteBuf.readVarInt();
        this.id = byteBuf.readUUID();

        this.input = byteBuf.readArray(Item.class, HandleByteBuf::readSlot);
        this.output = byteBuf.readArray(Item.class, HandleByteBuf::readSlot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) (windowId & 0xFF));
        byteBuf.writeVarInt(type);
        byteBuf.writeUUID(id);

        byteBuf.writeArray(input, byteBuf::writeSlot);
        byteBuf.writeArray(output, byteBuf::writeSlot);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
