package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class RiderJumpPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RIDER_JUMP_PACKET;

    /**
     * This is jumpStrength.
     * 对应跳跃进度条0-100
     * <p>
     * Corresponds to jump progress bars 0-100
     */
    public int unknown;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.unknown = byteBuf.readVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(this.unknown);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
