package cn.nukkit.network.protocol;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author joserobjr
 */


@ToString
@NoArgsConstructor(onConstructor = @__())
public class PositionTrackingDBClientRequestPacket extends DataPacket {


    public static final byte NETWORK_ID = ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET;
    
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
    public void encode() {
        reset();
        putByte((byte) action.ordinal());
        putVarInt(trackingId);
    }

    @Override
    public void decode() {
        int aByte = getByte();
        action = ACTIONS[aByte];
        trackingId = getVarInt();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum Action {


        QUERY
    }
}
