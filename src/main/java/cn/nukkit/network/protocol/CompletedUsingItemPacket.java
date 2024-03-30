package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompletedUsingItemPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.COMPLETED_USING_ITEM_PACKET;

    public static final int ACTION_UNKNOWN = -1;
    public static final int ACTION_EQUIP_ARMOR = 0;
    public static final int ACTION_EAT = 1;
    public static final int ACTION_ATTACK = 2;
    public static final int ACTION_CONSUME = 3;
    public static final int ACTION_THROW = 4;
    public static final int ACTION_SHOOT = 5;
    public static final int ACTION_PLACE = 6;
    public static final int ACTION_FILL_BOTTLE = 7;
    public static final int ACTION_FILL_BUCKET = 8;
    public static final int ACTION_POUR_BUCKET = 9;
    public static final int ACTION_USE_TOOL = 10;
    public static final int ACTION_INTERACT = 11;
    public static final int ACTION_RETRIEVE = 12;
    public static final int ACTION_DYED = 13;
    public static final int ACTION_TRADED = 14;


    public static final int ACTION_BRUSHING_COMPLETED = 15;

    public int itemId;
    public int action;


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeShortLE(itemId);
        byteBuf.writeIntLE(action);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
