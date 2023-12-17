package cn.nukkit.network.protocol;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author joserobjr
 */


@ToString
@NoArgsConstructor(onConstructor = @__())
public class ItemStackResponsePacket extends DataPacket {


    public static final byte NETWORK_ID = ProtocolInfo.ITEM_STACK_RESPONSE_PACKET;

    @Override
    public void encode() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public void decode() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
