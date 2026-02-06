package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlockEventPacket extends DataPacket {
    public int x;
    public int y;
    public int z;
    public int type;
    public int value;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.type);
        byteBuf.writeVarInt(this.value);
    }

    @Override
    public int pid() {
        return ProtocolInfo.BLOCK_EVENT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
