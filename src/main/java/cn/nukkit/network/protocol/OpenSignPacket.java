package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@Builder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpenSignPacket extends DataPacket {
    public BlockVector3 position;
    public boolean frontSide;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.position = byteBuf.readBlockVector3();
        this.frontSide = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBlockVector3(position);
        byteBuf.writeBoolean(frontSide);
    }

    @Override
    public int pid() {
        return ProtocolInfo.OPEN_SIGN;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
