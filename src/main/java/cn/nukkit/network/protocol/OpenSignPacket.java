package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpenSignPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.OPEN_SIGN;
    public BlockVector3 position;
    public boolean frontSide;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
