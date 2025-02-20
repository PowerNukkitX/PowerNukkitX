package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RiderJumpPacket extends DataPacket {
    /**
     * Corresponds to jump progress bars 0-100
     */
    public int jumpStrength;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.jumpStrength = byteBuf.readVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.jumpStrength);
    }

    @Override
    public int pid() {
        return ProtocolInfo.RIDER_JUMP_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
