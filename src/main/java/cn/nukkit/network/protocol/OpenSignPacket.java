package cn.nukkit.network.protocol;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.math.BlockVector3;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
public class OpenSignPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.OPEN_SIGN;
    private BlockVector3 position;
    private boolean frontSide;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.position = getBlockVector3();
        this.frontSide = getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        putBlockVector3(position);
        putBoolean(frontSide);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
