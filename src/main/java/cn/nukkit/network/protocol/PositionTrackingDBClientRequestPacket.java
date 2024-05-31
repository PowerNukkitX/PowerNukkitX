package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author joserobjr
 */


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PositionTrackingDBClientRequestPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET;

    private static final Action[] ACTIONS = Action.values();

    public Action action;
    public int trackingId;

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) action.ordinal());
        byteBuf.writeVarInt(trackingId);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        int $2 = byteBuf.readByte();
        action = ACTIONS[aByte];
        trackingId = byteBuf.readVarInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public enum Action {


        QUERY
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
