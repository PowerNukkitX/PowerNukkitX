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
    public static final int NETWORK_ID = ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET;

    private static final Action[] ACTIONS = Action.values();

    public Action action;
    public int trackingId;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) action.ordinal());
        byteBuf.writeVarInt(trackingId);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int aByte = byteBuf.readByte();
        action = ACTIONS[aByte];
        trackingId = byteBuf.readVarInt();
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public enum Action {


        QUERY
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
