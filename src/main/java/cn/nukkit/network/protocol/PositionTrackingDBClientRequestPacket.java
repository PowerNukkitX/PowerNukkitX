package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author joserobjr
 */


@ToString
@NoArgsConstructor(onConstructor = @__())
public class PositionTrackingDBClientRequestPacket extends DataPacket {


    public static final int NETWORK_ID = ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET;

    private static final Action[] ACTIONS = Action.values();

    private Action action;
    private int trackingId;


    public Action getAction() {
        return action;
    }

    public int getTrackingId() {
        return trackingId;
    }

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
