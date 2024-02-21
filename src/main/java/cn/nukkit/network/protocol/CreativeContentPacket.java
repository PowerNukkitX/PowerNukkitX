package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;


@ToString
public class CreativeContentPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;


    public Item[] entries = Item.EMPTY_ARRAY;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(entries.length);
        for (int i = 0; i < entries.length; i++) {
            this.putUnsignedVarInt(i + 1);//netId
            this.putSlot(entries[i], true);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
