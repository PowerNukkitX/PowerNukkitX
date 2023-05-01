package cn.nukkit.network.protocol.v575;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
@Deprecated(since = "1.19.80-r1")
public class RequestChunkRadiusPacket_v575 extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;

    public int radius;

    @Override
    public void decode() {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
