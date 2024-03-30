package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreativeContentPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;


    public Item[] entries = Item.EMPTY_ARRAY;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(entries.length);
        for (int i = 0; i < entries.length; i++) {
            byteBuf.writeUnsignedVarInt(i + 1);//netId
            byteBuf.writeSlot(entries[i], true);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
